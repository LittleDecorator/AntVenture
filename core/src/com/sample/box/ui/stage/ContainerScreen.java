package com.sample.box.ui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.sample.box.character.Warrior;
import com.sample.box.helpers.GameHelper;
import com.sample.box.helpers.ScreenHelper;
import com.sample.box.helpers.TextureHelper;
import com.sample.box.ui.drag.DragSource;
import com.sample.box.ui.drag.DragTarget;
import com.sample.box.ui.actor.ImageActor;
import com.sample.box.ui.entity.Item;
import com.sample.box.entities.Container;
import com.sample.box.ui.entity.Slot;

public class ContainerScreen implements Screen {

//    private Table lootDialog;
    private Window lootDialog;

    Table stash;                                            //player table
    Table target;                                           //container table

    DragAndDrop dragAndDrop;                                //drag and drop

    public static Stage stage;                              //screen stage

    private static Container sourceContainer;

    private Array<Item> containerItems;              //incoming container item array
    private Array<Slot> containerSlots = new Array<Slot>();              //container slot array
    private Array<Item> inventoryItems;              //player item array
    private Array<Slot> inventorySlots = new Array<Slot>();              //player slot array

    private boolean needRender = false;

    private void init(){
        stage = new Stage();
        Skin skin = new Skin(Gdx.files.internal("assets/skins/uiskin.json"));
        dragAndDrop = new DragAndDrop();
        lootDialog = createLootDialog(skin);
        stage.addActor(lootDialog);
    }

    @Override
    public void show() {
        if(stage==null){
            init();                 //init if stage not exists
        }
        fillContainer();            //every show fill container table
        fillInventory();            //every show fill player table
        Gdx.input.setInputProcessor(stage);
        lootDialog.setVisible(true);
        setNeedRender(true);
    }

    @Override
    public void render(float delta) {
        if(needRender){
            Table.drawDebug(stage);
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        lootDialog.setVisible(false);
        Gdx.input.setInputProcessor(GameHelper.getGame().getGip());
        setNeedRender(false);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    }

    public void setNeedRender(boolean needRender) {
        this.needRender = needRender;
    }

    //create window
//    private Table createLootDialog(Skin skin){
    private Window createLootDialog(Skin skin){

        Window dialog = new Window("Loot dialog", skin);
//        Table dialog = new Table(skin);
        dialog.debug();
//        dialog.setBackground(getGray());
        addClose(dialog, skin);           // add header with close button

        dialog.setPosition(300, 250);
        dialog.setSize(620, 320);

        dialog.setMovable(true);

        stash = new Table(skin);  //table for character stash items
        target = new Table(skin); //table for target items

        stash.debug();
        target.debug();

        //add tables to container
        //set stash table
        Cell cStash = dialog.add(stash);
        cStash.width(300).height(300).space(10);

        //set target table
        Cell cTarget = dialog.add(target);
        cTarget.width(300).height(300).space(10);

        //now try fill inner tables
        createTab(stash, skin, "Player Stash", inventorySlots);
        createTab(target, skin, "Target Stash", containerSlots);

        return dialog;
    }

    //pixmap drawable
    /*private Drawable getGray(){
        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(Color.LIGHT_GRAY);
        pm1.fill();
        return new TextureRegionDrawable(new TextureRegion(new Texture(pm1)));
    }*/

    //fill table cells with actor containers
    private void createTab(Table table, Skin skin, String header, Array<Slot> array){
        TextField field = new TextField(header,skin);
        field.setDisabled(true);        //set disabled (can't write)
        table.add(field).height(50).colspan(6).fillX();
        table.row();
        for(int i=0 ; i<5;i++){
            for(int j=0;j<6;j++){
                Slot slot = new Slot();
                dragAndDrop.addSource(new DragSource(slot));
                dragAndDrop.addTarget(new DragTarget(slot));
                table.add(slot).width(50).height(50);
                array.add(slot);
            }
            table.row();
        }
    }

    //add header and close button
    private void addClose(Window w, Skin skin){
        TextButton closeButton = new TextButton("X", skin);
        closeButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                //try update container inventory
                sourceContainer.setInventory(updateContainer());
                //try update player staff inventory
                Warrior.setInventory(updateInventory());
                ScreenHelper.getContainer().hide();
            }
        });
        w.getButtonTable().add(closeButton).height(w.getPadTop());
    }

    //prepare array for container
    private Array<Item> updateContainer(){
        Array<Item> res = new Array<Item>();
        //foreach cell we create slot
        for(Slot s : containerSlots){
            if(!s.isEmpty()){
                res.add(s.getItem());
            }
        }
        return res;
    }

    //prepare array for player
    private Array<Item> updateInventory(){
        Array<Item> res = new Array<Item>();
        //foreach cell we create slot
        for(Slot s : inventorySlots){
            if(!s.isEmpty()){
                res.add(s.getItem());
            }
        }
        return res;
    }

    //fill container slots
    public void fillContainer(){
        //get content from container object
        containerItems = sourceContainer.getInventory();
        for(int i=0;i<containerItems.size;i++){
            containerSlots.get(i).setItem(containerItems.get(i));
        }
    }

    //fill inventory stack actors
    public void fillInventory(){
        inventoryItems = Warrior.getInventory();
        for(int i=0;i<inventoryItems.size;i++){
            inventorySlots.get(i).setItem(inventoryItems.get(i));
        }
    }

    //set container source
    public static void setContainerSource(Container containerSource) {
        ContainerScreen.sourceContainer = containerSource;
    }

}
