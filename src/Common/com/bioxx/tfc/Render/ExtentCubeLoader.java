package com.bioxx.tfc.Render;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import jdk.nashorn.internal.runtime.events.RuntimeEvent;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import scala.util.parsing.input.StreamReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;

public class ExtentCubeLoader implements ICustomModelLoader {

    public static final ExtentCubeLoader INSTANCE = new ExtentCubeLoader();

    private static final Constructor<? extends IModel> vanillaModelWrapper;
    private static final Object vanillaLoader;
    private static final MethodHandle loaderGetter;
    private static final Field modelBlockField;
    private static final Field locationField;
    private static final Field uvLockField;
    private static final Field animationField;

    private IResourceManager manager;

    static
    {
        try
        {
            Class clas = Class.forName( ModelLoader.class.getName() + "$VanillaModelWrapper" );
            vanillaModelWrapper = clas.getDeclaredConstructor( ModelLoader.class, ResourceLocation.class, ModelBlock.class, boolean.class, ModelBlockAnimation.class );
            vanillaModelWrapper.setAccessible( true );
            modelBlockField = clas.getField("model");
            modelBlockField.setAccessible(true);
            locationField = clas.getField("location");
            locationField.setAccessible(true);
            uvLockField = clas.getField("uvlock");
            uvLockField.setAccessible(true);
            animationField = clas.getField("animation");
            animationField.setAccessible(true);

            Class<?> vanillaLoaderClass = Class.forName( ModelLoader.class.getName() + "$VanillaLoader" );
            Field instanceField = vanillaLoaderClass.getField( "INSTANCE" );
            // Static field
            vanillaLoader = instanceField.get( null );
            Field loaderField = vanillaLoaderClass.getDeclaredField( "loader" );
            loaderField.setAccessible( true );
            loaderGetter = MethodHandles.lookup().unreflectGetter( loaderField );
        }
        catch( Exception e )
        {
            throw Throwables.propagate( e );
        }
    }

    public static ModelBlock extractModelBlock(IModel model) {
        try {
            Object blockObj = modelBlockField.get(model);
            if (blockObj == null)
                return null;
            return (ModelBlock)blockObj;
        } catch (IllegalAccessException e) {
            Throwables.propagate(e);
            return null;
        }
    }

    private static IModel createVanillaWrapper(ResourceLocation location, ModelBlock block, boolean uvLock, ModelBlockAnimation animation) {
        try {
            ModelLoader loader = (ModelLoader)loaderGetter.invoke(vanillaLoader);
            return vanillaModelWrapper.newInstance(loader, location, block, uvLock, animation);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static IModel cloneVanillaWrapper(IModel model) {
        try {
            ModelBlock block = (ModelBlock)modelBlockField.get(model);
            ResourceLocation location = (ResourceLocation)locationField.get(model);
            boolean uvlock = (boolean)uvLockField.get(model);
            ModelBlockAnimation animation = (ModelBlockAnimation)animationField.get(model);

            ModelBlock newBlock = new ModelBlock(block.getParentLocation(), block.getElements(), block.textures, block.isAmbientOcclusion(), block.isGui3d(), block.getAllTransforms(), Lists.newArrayList(block.getOverrides()));
            newBlock.parent = block.parent;
            newBlock.name = block.name;

            return createVanillaWrapper(location, newBlock, uvlock, animation);
        } catch (IllegalAccessException e) {
            Throwables.propagate(e);
            return null;
        }
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        String path = modelLocation.getResourcePath();
        return modelLocation.getResourceDomain().equals("terrafirmacraft") && path.startsWith("minecraft_cube");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        String path = modelLocation.getResourcePath().substring("minecraft_".length());
        IResource minecraftModel = manager.getResource(new ResourceLocation("minecraft", path));
        InputStreamReader reader =  new InputStreamReader(minecraftModel.getInputStream(), StandardCharsets.UTF_8);
        ModelBlock block = ModelBlock.deserialize(reader);
        ResourceLocation armatureLocation = new ResourceLocation(modelLocation.getResourceDomain(), "armatures/" + path + ".json");
        ModelBlockAnimation animation = ModelBlockAnimation.loadVanillaAnimation(manager, armatureLocation);

        return new ExtentCubeModel(createVanillaWrapper(modelLocation, block, false, animation));
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        manager = resourceManager;
    }
}
