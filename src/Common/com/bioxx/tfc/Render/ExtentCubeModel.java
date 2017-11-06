package com.bioxx.tfc.Render;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.animation.IClip;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class ExtentCubeModel implements IModel {

    private IModel wrappedModel;

    public ExtentCubeModel(IModel model) {
        this.wrappedModel = model;
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return wrappedModel.getTextures();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return wrappedModel.bake(state, format, bakedTextureGetter);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return wrappedModel.getDependencies();
    }

    @Override
    public IModelState getDefaultState() {
        return wrappedModel.getDefaultState();
    }

    @Override
    public Optional<? extends IClip> getClip(String name) {
        return wrappedModel.getClip(name);
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData) {

        if (customData.size() == 0)
            return this;

        IModel processedBlock = wrappedModel.process(customData);
        if (wrappedModel == processedBlock)
            processedBlock = ExtentCubeLoader.cloneVanillaWrapper(processedBlock);

        ModelBlock block = ExtentCubeLoader.extractModelBlock(processedBlock);
        BlockPart part = block.getElements().get(0);

        if (customData.containsKey("minx")) {
            float minx = Float.parseFloat(customData.get("minx"));
            part.positionFrom.x = minx;
        }

        if (customData.containsKey("miny")) {
            float miny = Float.parseFloat(customData.get("miny"));
            part.positionFrom.y = miny;
        }

        if (customData.containsKey("minz")) {
            float minz = Float.parseFloat(customData.get("minz"));
            part.positionFrom.z = minz;
        }

        if (customData.containsKey("maxx")) {
            float maxx = Float.parseFloat(customData.get("maxx"));
            part.positionTo.x = maxx;
        }

        if (customData.containsKey("maxy")) {
            float maxy = Float.parseFloat(customData.get("maxy"));
            part.positionTo.y = maxy;
        }

        if (customData.containsKey("maxz")) {
            float maxz = Float.parseFloat(customData.get("maxz"));
            part.positionTo.z = maxz;
        }

        return new ExtentCubeModel(processedBlock);
    }

    @Override
    public IModel smoothLighting(boolean value) {
        return wrap(wrappedModel.smoothLighting(value));
    }

    @Override
    public IModel gui3d(boolean value) {
        return wrap(wrappedModel.gui3d(value));
    }

    @Override
    public IModel uvlock(boolean value) {
        return wrap(wrappedModel.uvlock(value));
    }

    @Override
    public IModel retexture(ImmutableMap<String, String> textures) {
        return wrap(wrappedModel.retexture(textures));
    }

    private IModel wrap(IModel model) {
        if (model == wrappedModel)
            return this;
        return new ExtentCubeModel(model);
    }
}
