package fr.mipiker.game.tiles;

import java.util.*;
import java.util.Map.Entry;
import fr.mipiker.game.*;
import fr.mipiker.isisEngine.*;
import fr.mipiker.isisEngine.loader.Assimp;
import static fr.mipiker.game.tiles.EnumCardinalPoint.*;

public abstract class Tile {

	protected static HashMap<EnumTiles, Texture[]> mapTileTypeTextures;
	protected static Mesh defaultMesh;

	public final EnumTiles TYLE_TYPE;

	protected ArrayList<EnumProperty> property = new ArrayList<>();
	protected Mesh mesh;
	protected PositionTile pos;
	protected Chunk belongChunk;
	protected EnumCardinalPoint orientation;

	private boolean mustUpdateSurroundingAfterThisUpdate;
	private Texture[] textures;
	private int actualTexture;

	///////////////////////

	protected Tile(EnumTiles TYLE_TYPE, Chunk belongChunk, PositionTile pos) {
		this.TYLE_TYPE = TYLE_TYPE;
		this.pos = pos;
		this.belongChunk = belongChunk;
		orientation = NORTH;
		textures = mapTileTypeTextures.get(TYLE_TYPE);
		mesh = Mesh.copy(defaultMesh);
		mesh.getMaterial().setTexture(textures[0]);
		translateMeshToPosition();
	}
	public static void load() {
		if (mapTileTypeTextures == null) {
			mapTileTypeTextures = new HashMap<>();
			defaultMesh = Assimp.loadModel("resources/tiles/tile.obj", null)[0];
			for (EnumTiles type : EnumTiles.values())
				mapTileTypeTextures.put(type, Texture.LoadTexture(type.getTexturesPath()));
		} else
			System.out.println("[WARNING] The mesh for tiles is already loaded");
	}

	///////////////////////

	protected abstract void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles);
	/**
	 * Update now the tile, reserved to be executed only by the map witch can order correctly the
	 * updates
	 */
	public void updateNow() {
		update(belongChunk.getBelongMap().getSurroundingTiles(pos));
		if (mustUpdateSurroundingAfterThisUpdate) {
			for (Entry<EnumCardinalPoint, Tile> entry : belongChunk.getBelongMap().getSurroundingTiles(pos).entrySet())
				if (entry.getValue() != null)
					entry.getValue().mustUpdate();
			mustUpdateSurroundingAfterThisUpdate = false;
		}
	}
	/**
	 * Update the tile and after update the surrouding tiles on the next map update
	 */
	public void mustUpdateWithSurrounding() {
		this.mustUpdate();
		this.mustUpdateSurroundingAfterThisUpdate = true;
	}
	public void mustUpdate() {
		belongChunk.addTileToUpdate(this);
	}

	///////////////////////

	protected abstract void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles);
	public void renderUpdateNow() {
		renderUpdate(belongChunk.getBelongMap().getSurroundingTiles(pos));
	}
	public void mustRenderUpdate() {
		belongChunk.addTileToRenderUpdate(this);
	}

	///////////////////////

	/**
	 * Translate the mesh to the correct position
	 */
	private void translateMeshToPosition() {
		mesh.getTransformation().setTranslation(pos.getWorldPos().x, 0, pos.getWorldPos().y);
	}
	/**
	 * Set a texture and rotate it
	 * 
	 * @param textureIndex
	 *            the index of textures array
	 * @param textureOrientation
	 *            the orientation in radians
	 */
	protected void setAndOrientTexture(int textureIndex, EnumCardinalPoint orientation) {
		setTexture(textureIndex);
		setOrientation(orientation);
	}
	/**
	 * Set the new texture from textures array
	 * 
	 * @param textureIndex
	 */
	protected void setTexture(int textureIndex) {
		actualTexture = textureIndex;
		mesh.getMaterial().setTexture(textures[actualTexture]);
		belongChunk.resetMeshOnUpdate();
	}
	protected int getActualTexture() {
		return actualTexture;
	}

	///////////////////////

	public EnumCardinalPoint getOrientation() {
		return orientation;
	}
	public void setOrientation(EnumCardinalPoint orientation) {
		this.orientation = orientation;
		mesh.getMaterial().setTextureRotation(orientation.getValue() * (float) Math.PI / 2);
		belongChunk.resetMeshOnUpdate();
	}
	public Mesh getMesh() {
		return mesh;
	}
	public Chunk getBelongChunk() {
		return belongChunk;
	}
	public PositionTile getPos() {
		return pos;
	}
	public ArrayList<EnumProperty> getProperty() {
		return property;
	}
	@Override
	public String toString() {
		return pos.toString() + "\nType : " + TYLE_TYPE + "\nOrientation : " + orientation;
	}
}

/*
 * How Update of tiles work :
 * 
 * When a tile is updated (manually or not), It update itself and if it is necessary, It will update
 * his surrounding tiles.
 */
