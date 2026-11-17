package de.raindancer.modules.warp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.raindancer.modules.warp.service.WarpTokens;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/** What the client is sent for the warp token: every file listed, the model whole, its textures there. */
class WarpPackTest {

    private static final Path ROOT = Path.of("src/main/resources" + WarpPack.ROOT);

    private static JsonObject json(String path) throws IOException {
        return JsonParser.parseString(Files.readString(ROOT.resolve(path))).getAsJsonObject();
    }

    @Test
    @DisplayName("the file list is exactly what is in the folder")
    void filesListed() throws IOException {
        List<String> found = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(ROOT)) {
            walk.filter(Files::isRegularFile).forEach(p -> found.add(ROOT.relativize(p).toString().replace('\\', '/')));
        }
        assertThat(new TreeSet<>(WarpPack.FILES)).isEqualTo(new TreeSet<>(found));
    }

    @Test
    @DisplayName("the token's item model points at a model in the pack, and every texture it uses is drawn")
    void tokenWhole() throws IOException {
        String[] key = WarpTokens.MODEL.split(":");
        JsonObject item = json("assets/" + key[0] + "/items/" + key[1] + ".json");
        String model = item.getAsJsonObject("model").get("model").getAsString();
        String[] modelKey = model.split(":");
        JsonObject geometry = json("assets/" + modelKey[0] + "/models/" + modelKey[1] + ".json");
        assertThat(geometry.getAsJsonArray("elements")).isNotEmpty();
        for (var texture : geometry.getAsJsonObject("textures").entrySet()) {
            String[] at = texture.getValue().getAsString().split(":");
            Path png = ROOT.resolve("assets/" + at[0] + "/textures/" + at[1] + ".png");
            assertThat(png).as(texture.getKey()).exists();
            BufferedImage image = javax.imageio.ImageIO.read(png.toFile());
            Path animation = ROOT.resolve("assets/" + at[0] + "/textures/" + at[1] + ".png.mcmeta");
            if (Files.exists(animation)) {
                assertThat(image.getHeight() % image.getWidth()).as(texture.getKey() + " frames").isZero();
            } else {
                assertThat(image.getHeight()).as(texture.getKey()).isEqualTo(image.getWidth());
            }
        }
        for (JsonElement element : geometry.getAsJsonArray("elements")) {
            for (var face : element.getAsJsonObject().getAsJsonObject("faces").entrySet()) {
                String used = face.getValue().getAsJsonObject().get("texture").getAsString().substring(1);
                assertThat(geometry.getAsJsonObject("textures").has(used)).as("#" + used).isTrue();
            }
        }
    }

    @Test
    @DisplayName("a pack the client takes: its format said, a description")
    void packMeta() throws IOException {
        JsonObject pack = json("pack.mcmeta").getAsJsonObject("pack");
        assertThat(pack.get("description").getAsString()).isNotBlank();
        assertThat(pack.has("min_format") || pack.has("pack_format")).isTrue();
    }
}
