package com.sample.box.ui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sample.box.entities.dialog.Choice;
import com.sample.box.helpers.DialogHelper;
import com.sample.box.helpers.GameHelper;
import com.sample.box.helpers.ScreenHelper;
import com.sample.box.ui.actor.LabelActor;

import java.util.Map;

public class DialogStage implements Screen, StageListener {

    private Window dialog;
    Table question;                                            //player table
    Table answers;                                           //container table

    public static Stage stage;

    private boolean needRender = false;

    private void init(){
        stage = new Stage();
        Skin skin = new Skin(Gdx.files.internal("assets/skins/uiskin.json"));

        dialog = createDialog(skin);
        stage.addActor(dialog);
    }

    @Override
    public void render(float delta) {
        if(needRender){
//            Table.drawDebug(stage);
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        if(stage==null){
            System.out.println("in chat init");
            init();
        }
        Gdx.input.setInputProcessor(stage);
        dialog.setVisible(true);
        setNeedRender(true);
    }

    @Override
    public void hide() {
        dialog.setVisible(false);
        Gdx.input.setInputProcessor(GameHelper.getGame().getGip());
        setNeedRender(false);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isVisible() {
        if(dialog == null){
            return false;
        } else {
            return dialog.isVisible();
        }
    }

    private Window createDialog(final Skin skin){
        Window dialog = new Window("Chat dialog", skin);
//        Table dialog = new Table(skin);
//        dialog.debug();
//        dialog.setBackground(getGray());
        addClose(dialog, skin);           // add header with close button

        dialog.setPosition(300, 250);
        dialog.setSize(620, 320);

        dialog.setMovable(true);

        question = new Table(skin);  //table for character stash items
        answers = new Table(skin); //table for target items

        question.debug();
        answers.debug();

        //add tables to container
        //set stash table
        Cell cStash = dialog.add(question);
        cStash.width(300).height(300).space(10);

        //set target table
        Cell cTarget = dialog.add(answers);
        cTarget.width(300).height(300).space(10);

        LabelActor qLabel = new LabelActor(DialogHelper.actualQuestion,skin);
        question.add(qLabel).height(50).colspan(5).fillX();
        question.row();

        for(Choice c : DialogHelper.actualAnswers){
            final LabelActor label = new LabelActor(c.getText(),skin);
            label.setId(c.getNext());
            label.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    DialogHelper.next(label.getId());
                    refreshDialog(skin);
                }
            });
            answers.add(label).height(50).colspan(5).fillX().row();
        }

        return dialog;
    }

    public void setNeedRender(boolean needRender) {
        this.needRender = needRender;
    }


    //add header and close button
    private void addClose(Window w, Skin skin){
        TextButton closeButton = new TextButton("X", skin);
        closeButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                //try update container inventory
                ScreenHelper.getChat().hide();
            }
        });
//        w.getButtonTable().add(closeButton).height(w.getPadTop());
        w.getTitleTable().add(closeButton).height(w.getPadTop());
    }

    private void refreshDialog(final Skin skin){
        LabelActor actor;
        if(DialogHelper.isOverDialog()){
            hide();
        } else {
            //question update
            actor = (LabelActor)question.getCells().get(0).getActor();
            actor.setText(DialogHelper.actualQuestion);
            //answers update
            answers.clearChildren();
            for(Choice c : DialogHelper.actualAnswers){
                final LabelActor label = new LabelActor(c.getText(),skin);
                label.setId(c.getNext());
                label.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        DialogHelper.next(label.getId());
                        refreshDialog(skin);
                    }
                });
                answers.add(label).height(50).colspan(5).fillX().row();
            }
        }
    }

}
