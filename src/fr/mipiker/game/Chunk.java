package fr.mipiker.game;

import static fr.mipiker.game.tiles.EnumProperty.ONLY_TICK_UPDATE;
import java.util.ArrayList;
import org.joml.Vector2i;
import fr.mipiker.game.tiles.*;
import fr.mipiker.isisEngine.*;
import fr.mipiker.isisEngine.utils.GreedyMeshUtils;

public class Chunk {

	public static final int SIZE = 10;
	private Vector2i pos;
	private Tile[][] tiles = new Tile[SIZE][SIZE];
	private Mesh[] meshesToRender;
	private Mesh[][] meshesTiles;
	private boolean resetMesh;
	private Map belongMap;
	private ArrayList<Tile> tileToUpdate = new ArrayList<>();
	private ArrayList<Tile> tileToRenderUpdate = new ArrayList<>();
	private Tile[] fixedTilesToUpdate;

	public Chunk(Vector2i pos, Map belongMap) {
		this.pos = pos;
		this.belongMap = belongMap;
		resetTiles();
		resetMeshesTiles();
		resetMeshesToRender();
		resetMeshesTiles();
	}

	public void update(Scene scene, boolean tickUpdate) {
		for (Tile tile : fixedTilesToUpdate) {
			if (((tile.getProperty().contains(ONLY_TICK_UPDATE) && tickUpdate)) || (!tile.getProperty().contains(ONLY_TICK_UPDATE))) {
				tile.updateNow();
			} else { // If this tile update on tick and this is not a tick update, put it to the next update
				addTileToUpdate(tile);
			}
		}
	}

	public void fixTilesToUpdate() {
		fixedTilesToUpdate = tileToUpdate.toArray(new Tile[tileToUpdate.size()]);
		tileToUpdate.clear();
	}

	public void renderUpdate(Scene scene) {

		Tile[] tileToRenderUpdate = this.tileToRenderUpdate.toArray(new Tile[this.tileToRenderUpdate.size()]);
		this.tileToRenderUpdate.clear();

		for (Tile tile : tileToRenderUpdate)
			tile.renderUpdateNow();

		// Reset the meshes if needed
		if (resetMesh) {
			for (Mesh mesh : meshesToRender)
				scene.removeMesh(mesh);
			resetMeshesToRender();
			for (Mesh mesh : meshesToRender)
				scene.addMesh(mesh);
			resetMesh = false;
		}
	}

	public void setTile(Tile tile) {
		resetMesh = true;
		tiles[tile.getPos().getChunkPos().x][tile.getPos().getChunkPos().y] = tile;
		meshesTiles[tile.getPos().getChunkPos().x][tile.getPos().getChunkPos().y] = tile.getMesh();
		tile.mustUpdateWithSurrounding();
		tile.mustRenderUpdate();
		for (Tile t : getBelongMap().getSurroundingTiles(tile.getPos()).values())
			t.mustRenderUpdate();
	}

	private void resetTiles() {
		for (int y = 0; y < SIZE; y++)
			for (int x = 0; x < SIZE; x++)
				tiles[x][y] = new Empty(this, new PositionTile(pos, new Vector2i(x, y)));
	}
	private void resetMeshesToRender() {
		ArrayList<ComposedMesh> meshesToRender = GreedyMeshUtils.applyGreedyMesh(meshesTiles);
		this.meshesToRender = new Mesh[meshesToRender.size()];
		for (int i = 0; i < meshesToRender.size(); i++)
			this.meshesToRender[i] = meshesToRender.get(i);
	}
	private void resetMeshesTiles() {
		meshesTiles = new Mesh[SIZE][SIZE];
		for (int y = 0; y < SIZE; y++)
			for (int x = 0; x < SIZE; x++)
				meshesTiles[x][y] = tiles[x][y].getMesh();
	}

	public Mesh[] getMeshesToRender() {
		return meshesToRender;
	}
	public Tile getTile(Vector2i chunkPos) {
		return tiles[chunkPos.x][chunkPos.y];
	}
	public Mesh[][] getMeshesTiles() {
		return meshesTiles;
	}
	public Map getBelongMap() {
		return belongMap;
	}
	public void resetMeshOnUpdate() {
		resetMesh = true;
	}

	public void addTileToUpdate(Tile tile) {
		if (!tileToUpdate.contains(tile)) {
			tileToUpdate.add(tile);
			belongMap.addChunkToUpdate(this);
		}
	}
	public void addTileToRenderUpdate(Tile tile) {
		if (!tileToRenderUpdate.contains(tile)) {
			tileToRenderUpdate.add(tile);
		}
	}

	public Vector2i getPos() {
		return pos;
	}

	@Override
	public String toString() {
		return "[Chunk] " + pos.x + " " + pos.y + " ";

	}
}
