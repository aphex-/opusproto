package com.nukethemoon.tools.opusproto.editor.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nukethemoon.tools.ani.Ani;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import com.nukethemoon.tools.ani.BaseAnimation;
import com.nukethemoon.tools.opusproto.SamplerLoader;
import com.nukethemoon.tools.opusproto.editor.Config;
import com.nukethemoon.tools.opusproto.editor.InputController;
import com.nukethemoon.tools.opusproto.editor.Settings;
import com.nukethemoon.tools.opusproto.editor.message.CommandDrawMap;
import com.nukethemoon.tools.opusproto.editor.message.CommandDrawRectangle;
import com.nukethemoon.tools.opusproto.editor.message.CommandGenerateWorld;
import com.nukethemoon.tools.opusproto.editor.message.CommandOpenSaveAs;
import com.nukethemoon.tools.opusproto.editor.message.CommandRefreshLayout;
import com.nukethemoon.tools.opusproto.editor.message.CommandRenameElement;
import com.nukethemoon.tools.opusproto.editor.message.CommandSaveProject;
import com.nukethemoon.tools.opusproto.editor.message.CommandSnapshot;
import com.nukethemoon.tools.opusproto.editor.message.CommandSnapshotBySampler;
import com.nukethemoon.tools.opusproto.editor.message.CommandSnapshotChangeSettings;
import com.nukethemoon.tools.opusproto.editor.message.CommandSnapshotDelete;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.CommandCreateInterpreter;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.CommandDeleteInterpreter;
import com.nukethemoon.tools.opusproto.editor.message.interpreter.EventInterpreterPoolChanged;
import com.nukethemoon.tools.opusproto.editor.message.layer.CommandCreateLayer;
import com.nukethemoon.tools.opusproto.editor.message.layer.CommandDeleteLayer;
import com.nukethemoon.tools.opusproto.editor.message.layer.EventLayersChanged;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandCreateSampler;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandDeleteSampler;
import com.nukethemoon.tools.opusproto.editor.message.sampler.CommandOpenSamplerEditor;
import com.nukethemoon.tools.opusproto.editor.message.sampler.EventSamplerPoolChanged;
import com.nukethemoon.tools.opusproto.editor.ui.Styles;
import com.nukethemoon.tools.opusproto.editor.ui.UI;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.BaseDialog;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.ErrorDialog;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.ProjectDialog;
import com.nukethemoon.tools.opusproto.editor.ui.dialogs.SaveAsDialog;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.AbstractSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.AContinentForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.CombinedSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.FlatSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.MaskedSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.formular.noise.NoiseSamplerForm;
import com.nukethemoon.tools.opusproto.editor.ui.sampler.previews.SamplerPreviewImage;
import com.nukethemoon.tools.opusproto.editor.ui.windows.SamplerEditor;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.exceptions.SamplerRecursionException;
import com.nukethemoon.tools.opusproto.generator.ChunkListener;
import com.nukethemoon.tools.opusproto.generator.WorldConfiguration;
import com.nukethemoon.tools.opusproto.generator.WorldGenerator;
import com.nukethemoon.tools.opusproto.interpreter.AbstractInterpreter;
import com.nukethemoon.tools.opusproto.interpreter.ColorInterpreter;
import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.layer.LayerConfig;
import com.nukethemoon.tools.opusproto.loader.json.JsonLoader;
import com.nukethemoon.tools.opusproto.noise.AbstractNoiseAlgorithm;
import com.nukethemoon.tools.opusproto.noise.NoiseAlgorithmPool;
import com.nukethemoon.tools.opusproto.noise.algorithms.CellNoise;
import com.nukethemoon.tools.opusproto.noise.algorithms.DiamondSquare;
import com.nukethemoon.tools.opusproto.noise.algorithms.SimplexNoise;
import com.nukethemoon.tools.opusproto.region.Chunk;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;
import com.nukethemoon.tools.opusproto.sampler.ChildSamplerConfig;
import com.nukethemoon.tools.opusproto.sampler.acontinent.AContinent;
import com.nukethemoon.tools.opusproto.sampler.combined.Combined;
import com.nukethemoon.tools.opusproto.sampler.flat.FlatSampler;
import com.nukethemoon.tools.opusproto.sampler.masked.MaskedSampler;
import com.nukethemoon.tools.opusproto.sampler.masked.MaskedSamplerConfig;
import com.nukethemoon.tools.opusproto.sampler.noise.NoiseConfig;
import com.nukethemoon.tools.opusproto.sampler.noise.NoiseSampler;
import com.nukethemoon.tools.opusproto.tools.Log;
import com.nukethemoon.tools.simpletask.SimpleTaskExecutor;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class Editor implements ApplicationListener, ChunkListener {

	private SpriteBatch batch;
	private ShapeRenderer worldShapeRenderer;
	private ShapeRenderer screenShapeRenderer;
	private OrthographicCamera camera;

	private Map<Layer, List<Sprite>> layerToSprites = new HashMap<Layer, List<Sprite>>();

	public static Map<Class<? extends AbstractSampler>, Class<? extends AbstractSamplerForm>> KNOWN_EDITORS;
	public static Map<String, Class<? extends AbstractNoiseAlgorithm>> KNOWN_ALGORITHMS = new HashMap<String, Class<? extends AbstractNoiseAlgorithm>>();

	private float blinkProgress = 0f;
	private Color blinkColor = new Color();

	private static LwjglApplicationConfiguration cfg;
	private UI ui;

	private WorldGenerator worldGenerator;

	private int windowWidth;
	private int windowHeight;

	private boolean worldRenderEnabled = false;

	private static Bus bus = new Bus(ThreadEnforcer.ANY);

	public static String DEFAULT_SAMPLER_NAME = "EditorDefaultSampler";
	public static String DEFAULT_MASK_SAMPLER_NAME = "EditorDefaultMask";
	public static String DEFAULT_INTERPRETER_NAME = "EditorDefaultInterpreter";

	private NoiseAlgorithmPool noisePool;
	private WorldConfiguration worldConfiguration;
	private SamplerLoader samplerLoader;

	private Vector3 tmpVector0 = new Vector3();
	private Vector3 tmpVector1 = new Vector3();
	private Vector3 tmpVector2 = new Vector3();
	private Vector3 tmpVector3 = new Vector3();
	private Vector3 tmpVector4 = new Vector3();

	private Sprite samplerSnapshot = null;
	private boolean samplerSnapshotDrawBehind = true;
	public static Stage STAGE;
	private JsonLoader fileOperation;
	private InputMultiplexer inputMultiplexer;
	private InputController inputController;
	private float performanceFactor = 1;

	private Color clearColor = Color.GRAY;

	private Ani ani;

	private static Settings settings;
	private Image splashScreen;


	public static void main(String [] args)	{
		cfg = new LwjglApplicationConfiguration();
		cfg.title = Config.NAME;
		cfg.width = 1300;
		cfg.height = 800;
		cfg.samples = 0;
		new EditorApplication(new Editor(), cfg);
	}

	public static void post(Object o) {
		bus.post(o);
	}

	@Override
	public void create() {

		bus.register(this);

		Styles.init();
		settings = Settings.load(Config.SETTINGS_FILE);

		cfg.width = settings.screenWidth;
		cfg.height = settings.screenHeight;
		windowHeight = cfg.height;
		windowWidth = cfg.width;
		Gdx.graphics.setDisplayMode(windowWidth, windowHeight, false);

		camera = new OrthographicCamera(windowWidth, windowHeight);
		camera.position.set(0, 0, 10);
		batch = new SpriteBatch();
		STAGE = new Stage(new ScreenViewport());
		worldShapeRenderer = new ShapeRenderer();
		worldShapeRenderer.setAutoShapeType(true);
		screenShapeRenderer = new ShapeRenderer();
		screenShapeRenderer.setAutoShapeType(true);
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(STAGE);
		Gdx.input.setInputProcessor(inputMultiplexer);


		KNOWN_EDITORS = new HashMap<Class<? extends AbstractSampler>, Class<? extends AbstractSamplerForm>>();
		KNOWN_EDITORS.put(NoiseSampler.class, 		NoiseSamplerForm.class);
		KNOWN_EDITORS.put(FlatSampler.class, 		FlatSamplerForm.class);
		KNOWN_EDITORS.put(Combined.class, 			CombinedSamplerForm.class);
		KNOWN_EDITORS.put(MaskedSampler.class, 		MaskedSamplerForm.class);
		KNOWN_EDITORS.put(AContinent.class, 		AContinentForm.class);

		KNOWN_ALGORITHMS.put(SimplexNoise.class.getSimpleName(), 	SimplexNoise.class);
		KNOWN_ALGORITHMS.put(DiamondSquare.class.getSimpleName(), 	DiamondSquare.class);
		KNOWN_ALGORITHMS.put(CellNoise.class.getSimpleName(), 		CellNoise.class);

		Log.out = new Log.Out() {
			@Override
			public void logError(String tag, String message) {
				Gdx.app.error(tag, message);
				showErrorDialog(message);
			}

			@Override
			public void logInfo(String tag, String message) {
				Gdx.app.log(tag, message);
			}

			@Override
			public void logDebug(String tag, String message) {
				Gdx.app.log(tag, message);
			}
		};

		ani = new Ani();

		setExitDialog();

		if (!Config.DEBUG) {
			splashScreen = new Image(new Texture(Config.IMAGE_PATH_SPLASH));
			splashScreen.setPosition(cfg.width / 2 - splashScreen.getWidth() / 2, cfg.height / 2 - splashScreen.getHeight() / 2);
			STAGE.addActor(splashScreen);
			AnimationFinishedListener listener = new AnimationFinishedListener() {
				@Override
				public void onAnimationFinished(BaseAnimation baseAnimation) {
					openLoadDialog();
				}
			};
			ani.add(new BaseAnimation(1500, listener) {
				@Override
				protected void onProgress(float v) {
					Color imageColor = splashScreen.getColor();
					imageColor.a = v;
					splashScreen.setColor(imageColor);
				}
			});
		} else {
			openLoadDialog();
		}

	}

	private void openLoadDialog() {
		if (!Gdx.files.local(Config.PROJECT_PATH + settings.openProject).exists()) {
			ProjectDialog dialog = new ProjectDialog(Styles.UI_SKIN, STAGE);
			dialog.show(STAGE);
			dialog.setResultListener(new BaseDialog.ResultListener() {
				@Override
				public void onResult(Object result) {
					init((String) result);
				}
			});
		} else {
			init(settings.openProject);
		}
	}


	public static void openHelp(String filename) {
		File htmlFile = new File(filename);
		try {
			Desktop.getDesktop().browse(htmlFile.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void openNTMPage() {
		try {
			URL url = new URL(Config.NTM_URL);
			Desktop.getDesktop().browse(url.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void setExitDialog() {
		if (settings.showExitDialog && !Config.DEBUG) {
			Dialog exitDialog = new Dialog("Confirm Exit", Styles.UI_SKIN)	{
				@Override
				protected void result(Object object)
				{
					if (object.equals(true))
					{
						((ExitDialogInterface) Gdx.app).exitConfirmed();
					} else
					{
						((ExitDialogInterface) Gdx.app).exitCanceled();
					}
				}
			};
			exitDialog.button("Yes", true);
			exitDialog.button("Cancel", false);
			((ExitDialogInterface) Gdx.app).setExitDialog(exitDialog, STAGE);
		}
	}

	private void init(String projectName) {

		if (splashScreen != null) {
			splashScreen.remove();
		}

		fileOperation = new JsonLoader();

		noisePool = new NoiseAlgorithmPool();
		samplerLoader = new SamplerLoader();

		samplerLoader.addInterpreter(new ColorInterpreter(DEFAULT_INTERPRETER_NAME));

		try {
			worldGenerator = fileOperation.load(samplerLoader, noisePool, Config.PROJECT_PATH + projectName + "/save.json");
		} catch (Exception e) {
			Log.e(Editor.class, e.getMessage());
			e.printStackTrace();
			showErrorDialog(e.getMessage());
		}

		worldConfiguration = worldGenerator.getConfig();
		worldGenerator.addRegionListener(this);

		// init a new projects
		if (worldGenerator.getLayers().size() == 0) {
			try {
				LayerConfig config = new LayerConfig("BaseLayer");
				config.interpreterId = DEFAULT_INTERPRETER_NAME;
				worldGenerator.getLayers().add(new Layer(config, worldConfiguration.seed, samplerLoader));
			} catch (SamplerInvalidConfigException e) {
				showErrorDialog(e.getMessage());
			}
			worldConfiguration.layerIds = new String[]{"BaseLayer"};
		}

		createEditorDefaultSampler();

		ui = new UI(STAGE, cfg, worldGenerator, noisePool, samplerLoader, bus, Styles.UI_SKIN, settings);
		bus.register(ui);

		generate(worldGenerator);

		worldRenderEnabled = true;

		inputController = new InputController(STAGE, camera, ui, worldGenerator, cfg);
		inputMultiplexer.addProcessor(inputController);


		/*SimplePositionConfig msc = new SimplePositionConfig("se");
		msc.gridSize = worldConfiguration.mapSize;
		msc.maximumDistance = msc.gridSize / 2;
		SimplePositionScattering scattering = new SimplePositionScattering(msc, worldConfiguration.seed, noisePool);
		points = scattering.createPoints(
				-worldConfiguration.mapSize * 3,
				-worldConfiguration.mapSize * 3,
				worldConfiguration.mapSize * 3,
		 		worldConfiguration.mapSize * 3, worldConfiguration.seed);*/

		clearColor = Color.BLACK;

	}

	private void createEditorDefaultSampler() {
		try {
			samplerLoader.addSampler(
					new NoiseSampler(new NoiseConfig(DEFAULT_SAMPLER_NAME),
							worldConfiguration.seed, noisePool, samplerLoader));

			MaskedSamplerConfig maskedSamplerConfig = new MaskedSamplerConfig(DEFAULT_MASK_SAMPLER_NAME);
			maskedSamplerConfig.samplerItems = new ChildSamplerConfig[2];
			maskedSamplerConfig.samplerItems[0] = new ChildSamplerConfig(DEFAULT_SAMPLER_NAME);
			maskedSamplerConfig.samplerItems[1] = new ChildSamplerConfig(DEFAULT_SAMPLER_NAME);
			samplerLoader.addSampler(
					new MaskedSampler(maskedSamplerConfig, worldConfiguration.seed, noisePool, samplerLoader)
			);
		} catch (SamplerInvalidConfigException e) {
			showErrorDialog("Error loading editor standard sampler.");
		}


	}

	@Subscribe
	@SuppressWarnings("unused")
	public void save(CommandSaveProject command) {
		fileOperation.save(samplerLoader, worldGenerator);
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void saveAs(CommandOpenSaveAs command) {
		SaveAsDialog saveAsDialog = new SaveAsDialog(Styles.UI_SKIN);
		saveAsDialog.setResultListener(new BaseDialog.ResultListener() {
			@Override
			public void onResult(Object result) {
				String name = (String) result;
				if (isValidName(name)) {
					if (!Gdx.files.local(Config.PROJECT_PATH  + name).exists()) {
						fileOperation.saveAs(samplerLoader, worldGenerator, name);
						updateWorldName(name);
					} else {
						showErrorDialog("Can not save as " + name + ". A project with this name exists already.");
					}
				} else {
					showErrorDialog("Can not save as " + name + ". Invalid name.");
				}
			}
		});
		saveAsDialog.show(STAGE);
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void createNewLayer(CommandCreateLayer command) {

		try {
			LayerConfig config = new LayerConfig(command.layerId);
			config.interpreterId = DEFAULT_INTERPRETER_NAME;
			Layer layer = new Layer(config, worldGenerator.getConfig().seed, samplerLoader);
			layerToSprites.put(layer, new ArrayList<Sprite>());
			worldGenerator.getLayers().add(layer);
			Editor.post(new EventLayersChanged());

		} catch (SamplerInvalidConfigException e) {
			Editor.showErrorDialog("Error creating layer.");
		}

		/*if (command.layerId == null || command.layerId.length() == 0) {
			ErrorDialog errorDialog = new ErrorDialog("Can not add layer. Invalid layer name.", UI_SKIN);
			errorDialog.show(STAGE);
			return;
		}

		for (int i = 0; i < worldGenerator.getLayers().size(); i++) {
			Layer layer = worldGenerator.getLayers().get(i);
			if (layer.getConfig().id.equals(command.layerId)) {
				ErrorDialog errorDialog = new ErrorDialog("Can not add layer. Name '" + command.layerId + "' is already used.", UI_SKIN);
				errorDialog.show(STAGE);
				return;
			}
		}

		Layer.LayerConfig config = new Layer.LayerConfig(command.layerId);
		config.samplerItems = new Layer.SamplerItemConfig[0];
		config.maskItems = new Layer.SamplerItemConfig[0];
		Layer l = new Layer(config, worldConfiguration.seed, samplerLoader);
		worldGenerator.getLayers().add(l);
		ui.getWorldEditor().reload();
		Editor.post(new CommandOpenLayerEditor(l.getConfig().id));*/
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void update(CommandGenerateWorld command) {
		if (command.force || settings.autoRefresh) {
			worldGenerator.clear();

			for (Layer l : worldGenerator.getLayers()) {
				for (Sprite s : layerToSprites.get(l)) {
					s.getTexture().dispose();
				}
			}

			layerToSprites.clear();
			generate(worldGenerator);
		}
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void deleteLayer(CommandDeleteLayer command) {
		Layer layerToDelete = null;
		for (Layer l : worldGenerator.getLayers()) {
			if (l.getConfig().id.equals(command.layerId)) {
				layerToDelete = l;
			}
		}
		worldGenerator.getLayers().remove(layerToDelete);
		Editor.post(new EventLayersChanged());
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void clearSnapShot(CommandSnapshotDelete command) {
		samplerSnapshot = null;
	}


	@Subscribe
	@SuppressWarnings("unused")
	public void takeSnapshot(CommandSnapshotBySampler command) {
		AbstractSampler sampler = samplerLoader.getSampler(command.id);
		takeSnapshot(sampler, command.drawBehind, command.opacity);
	}

	private CommandDrawRectangle commandDrawRectangle;

	@Subscribe
	@SuppressWarnings("unused")
	public void drawRect(CommandDrawRectangle command) {
		commandDrawRectangle = command;
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void endRect(CommandDrawMap command) {
		tmpVector4.set(command.x1, command.y1, 0);
		camera.unproject(tmpVector4);
		int x1 = worldToMap(tmpVector4.x);
		int y1 = worldToMap(tmpVector4.y);
		tmpVector4.set(command.x2, command.y2, 0);
		camera.unproject(tmpVector4);
		int x2 = worldToMap(tmpVector4.x);
		int y2 = worldToMap(tmpVector4.y);

		int lowX = x1 < x2 ? x1 : x2;
		int highX = x1 > x2 ? x1 : x2;
		int lowY = y1 < y2 ? y1 : y2;
		int highY = y1 > y2 ? y1 : y2;

		commandDrawRectangle = null;
		if ((highX - lowX) * (highY - lowY) > worldConfiguration.mapSize * (50 * performanceFactor)) {
			return;
		}

		List<int[]> positions = new ArrayList<int[]>();
		for (int x = lowX; x <= lowX + (highX - lowX); x++) {
			for (int y = lowY; y <= lowY + (highY - lowY); y++) {
				positions.add(new int[]{x, y});
			}
		}
		requestMap(positions);

	}

	private void requestMap(List<int[]> positions) {
		int[] regionX = new int[positions.size()];
		int[] regionY = new int[positions.size()];

		for (int i = 0; i < positions.size(); i++) {
			regionX[i] = positions.get(i)[0];
			regionY[i] = positions.get(i)[1];
		}
		requestMap(regionX, regionY);
	}

	private void takeSnapshot(AbstractSampler sampler, boolean drawBehind, float opacity) {
		if (opacity == 0) {
			samplerSnapshot = null;
			return;
		}
		ui.getSnapshotTable().setVisible(true);
		float zoom = camera.zoom;
		if (camera.zoom > 2.5) {
			zoom = 2.5f;
		}
		samplerSnapshotDrawBehind = drawBehind;
		int w = (int) (windowWidth * (zoom));
		int h = (int) (windowHeight * (zoom));
		int offsetX = (int) ((-w / 2) + camera.position.x);
		int offsetY = (int) ((-h / 2) + camera.position.y);
		Pixmap pixmap = SamplerPreviewImage.create(sampler, w, h, 1, offsetX, offsetY);
		samplerSnapshot = new Sprite(new Texture(pixmap));
		samplerSnapshot.setAlpha(opacity);
		samplerSnapshot.setPosition(offsetX, offsetY);
	}



	@Subscribe
	@SuppressWarnings("unused")
	public void changeSnapshotOrder(CommandSnapshotChangeSettings command) {
		samplerSnapshotDrawBehind = command.drawBehind;
		samplerSnapshot.setAlpha(command.opacity);
	}

	private void generate(WorldGenerator world) {
		int startSize = 1;

		layerToSprites.clear();
		for (Layer l : worldGenerator.getLayers()) {
			if (layerToSprites.get(l) == null) {
				layerToSprites.put(l, new ArrayList<Sprite>());
			}
		}

		List<int[]> positions = new ArrayList<int[]>();
		for (int mx = -startSize; mx < startSize; mx ++) {
			for (int my = -startSize; my < startSize; my ++) {
				double distance = Math.sqrt(Math.pow(0 - mx, 2) + Math.pow(0 - my, 2));
				if (distance <= startSize) {
					positions.add(new int[]{mx, my});
				}
			}
		}
		requestMap(positions);

		/*for (int i = 0; i < world.getLayers().size(); i++) {
			for (Chunk chunk : world.getLayers().get(i).getGeneratedRegions()) {
				addSprite(chunk, i);
			}
		}*/
	}

	private void requestMap(int x[], int y[]) {
		try {
			worldGenerator.requestChunks(x, y);
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onChunkCreated(int regionX, int regionY, Chunk chunk) {
		addSprite(chunk);
	}

	private void addSprite(final Chunk chunk) {
		if (chunk == null) {
			return;
		}

		SimpleTaskExecutor<Pixmap> executor = new SimpleTaskExecutor<Pixmap>();

		for (int layerIndex = 0; layerIndex < worldGenerator.getLayers().size(); layerIndex++) {
			final int finalLayerIndex = layerIndex;
			executor.addTask(new Callable<Pixmap>() {
				@Override
				public Pixmap call() throws Exception {
					return createPixmap(chunk.getLayerData()[finalLayerIndex], finalLayerIndex);
				}
			}, new SimpleTaskExecutor.ResultListener<Pixmap>() {
				@Override
				public void onResult(Pixmap result) {
					Sprite sprite = new Sprite(new Texture(result));
					result.dispose();
					sprite.setPosition(chunk.getOffsetX(), chunk.getOffsetY());
					Layer layer = worldGenerator.getLayers().get(finalLayerIndex);
					layerToSprites.get(layer).add(sprite);
				}
			});
		}

		try {
			executor.execute();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}



	public Pixmap createPixmap(float data[][], int layerIndex) {
		AbstractInterpreter interpreter = worldGenerator.getLayers().get(layerIndex).getInterpreter();
		Pixmap pixmap = new Pixmap(worldGenerator.getConfig().mapSize, worldGenerator.getConfig().mapSize, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.BLACK);
		for (int x = 0; x < worldGenerator.getConfig().mapSize; x++) {
			for (int y = 0; y < worldGenerator.getConfig().mapSize; y++) {
				float noise = data[x ][(worldGenerator.getConfig().mapSize - 1) - y];
				pixmap.drawPixel(x, y, interpreter.getType(noise));
			}
		}
		return pixmap;
	}

	@Override
	public void resize(int width, int height) {
		windowWidth = width;
		windowHeight = height;
		if (inputController != null) {
			inputController.setWindowWidth(windowWidth);
			inputController.setWindowHeight(windowHeight);
		}
		updateWindowSize(null);
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void updateWindowSize(CommandRefreshLayout c) {
		STAGE.getViewport().update(windowWidth, windowHeight, true);
		if (ui != null) {
			ui.updatePosition(windowWidth, windowHeight);
		}
		camera.viewportWidth = windowWidth;
		camera.viewportHeight = windowHeight;
		camera.update();
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void deleteSampler(CommandDeleteSampler command) {
		if (command.samplerId.equals(DEFAULT_MASK_SAMPLER_NAME) || command.samplerId.equals(DEFAULT_SAMPLER_NAME)) {
			showErrorDialog("You can not delete this sampler. It is a default layer for this editor.");
			return;
		}

		try {
			List<String> dependencies = new ArrayList<String>();
			if (samplerLoader.doesASamplerDependOn(samplerLoader.getSampler(command.samplerId), dependencies)) {
				showDependencyError(dependencies, "Sampler");
				return;
			}
		} catch (SamplerRecursionException e) {
			Log.e(Editor.class, e.getMessage());
			e.printStackTrace();
		}


		try {
			List<String> dependencies = new ArrayList<String>();
			if (samplerLoader.doesASamplerDependOn(samplerLoader.getSampler(command.samplerId),
					toAbstract(worldGenerator.getLayers()), dependencies, 0)) {
				showDependencyError(dependencies, "Layer");
				return;
			}

			samplerLoader.removeSampler(command.samplerId);
			Editor.post(new EventSamplerPoolChanged());

		} catch (SamplerRecursionException e) {
			Log.e(Editor.class, e.getMessage());
			e.printStackTrace();
		}
	}

	private static void showDependencyError(List<String> dependencies, String type) {
		String errorMessage = "Can not delete this sampler. A " + type + " depends on it.\n";
		for (String dependency : dependencies) {
			errorMessage += dependency + "\n";
		}
		ErrorDialog errorDialog = new ErrorDialog(errorMessage, Styles.UI_SKIN);
		errorDialog.show(STAGE);
	}

	public static List<AbstractSampler> toAbstract(List<Layer> layer) {
		List<AbstractSampler> list = new ArrayList<AbstractSampler>(layer.size());
		for (Layer l : layer) {
			list.add(l);
		}
		return list;
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void deleteInterpreter(CommandDeleteInterpreter command) {
		if (command.id.equals(DEFAULT_INTERPRETER_NAME)) {
			showErrorDialog("You can not delete the default interpreter.");
		}

		if (samplerLoader.doesALayerDependOn(samplerLoader.getInterpreter(command.id), worldGenerator.getLayers())) {
			showErrorDialog("You can not delete this interpreter. It is used in a layer.");
			return;
		}
		samplerLoader.removeInterpreter(command.id);
		Editor.post(new EventInterpreterPoolChanged());
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void takeSnapshot(CommandSnapshot command) {
		if (ui != null) {
			List<Window> windows = ui.getWindows();
			for (int i = 0; i < windows.size(); i++) {
				Window window = windows.get(i);
				if (window instanceof SamplerEditor) {
					if (window.isVisible()) {
						SamplerEditor e = ((SamplerEditor) window);
						String selectedSamplerId = e.getSelectedSamplerId();
						if (selectedSamplerId != null) {
							Editor.post(new CommandSnapshotBySampler(selectedSamplerId, 1, false));
						}
					}
				}
			}
		}

	}

	@Subscribe
	@SuppressWarnings("unused")
	public void createNewSampler(CommandCreateSampler command) {

		AbstractSampler s = samplerLoader.getSampler(command.name);
		if (s != null) {
			ErrorDialog errorDialog = new ErrorDialog("Can not create a sampler with the id " + command.name + ". This id is already used.", Styles.UI_SKIN);
			errorDialog.show(STAGE);
			return;
		}

		Class<? extends AbstractSamplerConfiguration> configClass = null;
		for (java.util.Map.Entry<Class<? extends AbstractSampler>, Class<? extends AbstractSamplerConfiguration>> entry : SamplerLoader.SAMPLER_TO_CONFIG.entrySet()) {
			if (entry.getKey().getSimpleName().toLowerCase().equals(command.type.toLowerCase())) {
				configClass = entry.getValue();
			}
		}

		if (configClass != null) {
			try {
				AbstractSamplerConfiguration o = (AbstractSamplerConfiguration) configClass.getConstructors()[0].newInstance(command.name);
				AbstractSampler sampler = SamplerLoader.create(o, worldConfiguration.seed, noisePool, samplerLoader);
				samplerLoader.addSampler(sampler);
				Editor.post(new EventSamplerPoolChanged());
				Editor.post(new CommandOpenSamplerEditor(sampler.getConfig().id));
			} catch (Exception e) {
				Gdx.app.log(Editor.class.getSimpleName(), "Error creating sampler. " + e.getMessage());
			}
		} else {
			Gdx.app.log(Editor.class.getSimpleName(), "Can not find config class for sampler " + command.type);
		}
	}

	@Override
	public void render() {

		handleCameraMovement();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		worldShapeRenderer.setProjectionMatrix(camera.combined);
		screenShapeRenderer.setProjectionMatrix(STAGE.getCamera().combined);

		Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



		if (worldRenderEnabled) {
			blinkProgress = (blinkProgress + 0.04f) % 1f;
			drawGrid();
			batch.begin();
			if (samplerSnapshot != null && samplerSnapshotDrawBehind) {
				samplerSnapshot.draw(batch);
			}
			for (Layer l : worldGenerator.getLayers()) {
				for (Sprite sprite : layerToSprites.get(l)) {
					sprite.draw(batch);
				}
			}

			if (samplerSnapshot != null && !samplerSnapshotDrawBehind) {
				samplerSnapshot.draw(batch);
			}
			batch.end();


			drawOrigin();
			drawTileSelection();
			drawCameraCenter();
			drawSelectionRect();

			drawPoints();

		}
		ani.update();

		STAGE.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		STAGE.draw();
	}

	private void drawPoints() {

		/*screenShapeRenderer.begin();

		for (int index = 0; index < points.length; index = index + 2) {
			float x = points[index];
			float y = points[index + 1];
			tmpVector1.set(x, y, 0);
			camera.project(tmpVector1);

			screenShapeRenderer.setColor(Color.BLUE);
			screenShapeRenderer.rect(tmpVector1.x - 1, tmpVector1.y - 1, 1, 1);

		}
		screenShapeRenderer.end();*/
	}

	private void drawSelectionRect() {
		if (commandDrawRectangle != null) {
			screenShapeRenderer.begin();
			float y1 = windowHeight - commandDrawRectangle.y1;
			float x1 = commandDrawRectangle.x1;
			float height = (windowHeight - commandDrawRectangle.y2) - y1;
			float width = commandDrawRectangle.x2 - commandDrawRectangle.x1;
			screenShapeRenderer.rect(x1, y1, width, height);
			screenShapeRenderer.end();
		}
	}

	private void drawOrigin() {
		tmpVector2.set(0, 0, 0);
		camera.project(tmpVector2, 0, 0, windowWidth, windowHeight);
		screenShapeRenderer.begin();
		screenShapeRenderer.setColor(Color.RED); // x
		screenShapeRenderer.line(tmpVector2.x, tmpVector2.y, 0, tmpVector2.x + 80, tmpVector2.y, 0);
		screenShapeRenderer.line(tmpVector2.x, tmpVector2.y + 1, 0, tmpVector2.x + 80, tmpVector2.y + 1, 0);
		screenShapeRenderer.setColor(Color.YELLOW); // y
		screenShapeRenderer.line(tmpVector2.x, tmpVector2.y, 0, tmpVector2.x, tmpVector2.y + 80, 0);
		screenShapeRenderer.line(tmpVector2.x + 1, tmpVector2.y, 0, tmpVector2.x + 1, tmpVector2.y + 80, 0);
		screenShapeRenderer.end();
	}

	private void drawTileSelection() {
		int x = Math.round(inputController.getMouseWorldPosition().x - 0.5f);
		int y = Math.round(inputController.getMouseWorldPosition().y - 0.5f);
		tmpVector3.set(x, y, 0);
		camera.project(tmpVector3);
		screenShapeRenderer.begin();
		blinkColor.set(blinkProgress, blinkProgress, blinkProgress, 1f);
		screenShapeRenderer.setColor(blinkColor); // x
		screenShapeRenderer.rect(tmpVector3.x, tmpVector3.y, 1 / camera.zoom, 1 / camera.zoom);
		screenShapeRenderer.end();
	}

	private void drawGrid() {
		screenShapeRenderer.begin();
		tmpVector1.set(camera.position.x - ((windowWidth / 2) * camera.zoom),
				camera.position.y + ((windowHeight / 2) * camera.zoom), 0);
		camera.project(tmpVector1);
		float screenLeft = tmpVector1.x;
		float screenTop = tmpVector1.y;
		tmpVector1.set(camera.position.x + ((windowWidth / 2) * camera.zoom),
				camera.position.y - ((windowHeight / 2) * camera.zoom), 0);
		camera.project(tmpVector1);
		screenShapeRenderer.setColor(Styles.GRID_COLOR_DARK);
		float screenRight = tmpVector1.x;
		float screenBottom = tmpVector1.y;
		float screenWidth = Math.abs(screenRight - screenLeft);
		float screenHeight = Math.abs(screenTop + screenBottom);
		int linesCountX = (int) ((screenWidth * camera.zoom) / worldConfiguration.mapSize) + 4;
		int linesCountY = (int) ((screenHeight * camera.zoom) / worldConfiguration.mapSize) + 4;
		int linesOffsetX = (int) (camera.position.x / worldConfiguration.mapSize);
		int linesOffsetY = (int) (camera.position.y / worldConfiguration.mapSize);
		if (linesCountX < 100 && linesCountY < 100) {
			for (int x = -linesCountX / 2; x < linesCountX / 2; x++) {
				for (int y = -linesCountY / 2; y < linesCountY / 2; y++) {
					int offsetX = x + linesOffsetX;
					int offsetY = y + linesOffsetY;
					// horizontal lines
					tmpVector0.set(camera.position.x - screenWidth * camera.zoom, worldConfiguration.mapSize * offsetY, 0);
					tmpVector1.set(camera.position.x + screenWidth * camera.zoom, worldConfiguration.mapSize * offsetY, 0);
					camera.project(tmpVector1);
					camera.project(tmpVector0);
					screenShapeRenderer.line(tmpVector0.x, tmpVector0.y, tmpVector1.x, tmpVector1.y);
					// vertical lines
					tmpVector0.set(worldConfiguration.mapSize * offsetX, camera.position.y - screenHeight * camera.zoom, 0);
					tmpVector1.set(worldConfiguration.mapSize * offsetX, camera.position.y + screenHeight * camera.zoom, 0);
					camera.project(tmpVector1);
					camera.project(tmpVector0);
					screenShapeRenderer.line(tmpVector0.x, tmpVector0.y, tmpVector1.x, tmpVector1.y);
				}
			}
		}
		screenShapeRenderer.end();
	}

	private void drawCameraCenter() {
		screenShapeRenderer.begin();
		blinkColor.set(1f - blinkProgress, 1f - blinkProgress, 1f - blinkProgress, 1f);
		screenShapeRenderer.setColor(blinkColor); // x
		float screenCenterX = windowWidth / 2;
		float screenCenterY = windowHeight / 2;
		screenShapeRenderer.line(screenCenterX - 10, screenCenterY, screenCenterX + 10, screenCenterY);
		screenShapeRenderer.line(screenCenterX, screenCenterY - 10, screenCenterX, screenCenterY + 10);
		screenShapeRenderer.end();

	}

	private void handleCameraMovement() {
		if (inputController == null) {
			return;
		}

		for (Integer keycode : inputController.getKeysDown()) {
			float movement = camera.zoom * 4;
			if (keycode == 19) { // up
				camera.position.add(0, movement, 0);
			}
			if (keycode == 20) { // down
				camera.position.add(0, -movement, 0);
			}
			if (keycode == 21) { // left
				camera.position.add(-movement, 0, 0);
			}
			if (keycode == 22) { // right
				camera.position.add(movement, 0, 0);
			}
		}
	}


	private int worldToMap(float coordinate) {
		int x = (int) (coordinate / worldGenerator.getConfig().mapSize);
		if (coordinate < 0) {
			x = x-1;
		}
		return x;
	}


	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		Settings settings = new Settings();
		settings.screenHeight = windowHeight;
		settings.screenWidth = windowWidth;
		if (ui != null) {
			settings.windows = ui.createWindowSettings();
		}
		if (fileOperation != null) {
			Settings.save(settings, Config.SETTINGS_FILE);
		}
	}


	public static boolean isValidName(String name) {
		return (name != null && name.length() > 0 && name.length() < 20);
	}

	public static void showErrorDialog(String text) {
		ErrorDialog errorDialog = new ErrorDialog(text, Styles.UI_SKIN);
		errorDialog.show(STAGE);
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void renameElement(CommandRenameElement command) {

		/*ErrorDialog dialog = new ErrorDialog("Not implemented", UI_SKIN);
		dialog.show(STAGE);*/

		if (!isValidName(command.newName)) {
			showErrorDialog("The name is not valid.");
			return;
		}

		if (command.type == CommandRenameElement.ElementType.Sampler) {
			if (samplerLoader.getSampler(command.newName) != null) {
				showErrorDialog("A sampler with the name " + command.newName + " exists already.");
				return;
			}
			if (command.oldName.equals(DEFAULT_MASK_SAMPLER_NAME) || command.oldName.equals(DEFAULT_SAMPLER_NAME)) {
				showErrorDialog("You can not rename this sampler. It is a default layer for this editor.");
				return;
			}


			samplerLoader.changeId(command.oldName, command.newName);

			for (Layer layer : worldGenerator.getLayers()) {
				//Layer.LayerConfig config = (Layer.LayerConfig) layer.getConfig();
				samplerLoader.changeId(layer, command.oldName, command.newName);
				try {
					layer.init();
				} catch (SamplerInvalidConfigException e) {
					showErrorDialog("Error renaming layer child. " + e.getMessage());
					e.printStackTrace();
				}

			}
			Editor.post(new EventSamplerPoolChanged());
		}

		if (command.type == CommandRenameElement.ElementType.Layer) {
			if (worldGenerator.getLayer(command.newName) != null) {
				showErrorDialog("A layer with the name " + command.newName + " exists already.");
				return;
			}
			Layer layer = worldGenerator.getLayer(command.oldName);
			layer.getConfig().id = command.newName;
			Editor.post(new EventLayersChanged());
		}

		if (command.type == CommandRenameElement.ElementType.Interpreter) {
			if (command.oldName.equals(DEFAULT_INTERPRETER_NAME)) {
				showErrorDialog("You can not rename the default interpreter.");
				return;
			}
			samplerLoader.changeInterpreterId(command.oldName, command.newName, worldGenerator.getLayers());
			Editor.post(new EventInterpreterPoolChanged());
		}
	}

	@Subscribe
	@SuppressWarnings("unused")
	public void createInterpreter(CommandCreateInterpreter command) {
		if (samplerLoader.getInterpreter(command.id) != null) {
			showErrorDialog("Can not create interpreter. The id '" + command.id + "' is already used.");
		} else {
			if (isValidName(command.id)) {
				samplerLoader.addInterpreter(new ColorInterpreter(command.id));
				Editor.post(new EventInterpreterPoolChanged());
			} else {
				showErrorDialog("Invalid name '" + command.id + "'");
			}
		}
	}

	private void updateWorldName(String name) {
		worldConfiguration.name = name;
		ui.getWorldEditor().setWorldName(name);
		ui.getProjectWindow().setWorldName(name);
	}



}
