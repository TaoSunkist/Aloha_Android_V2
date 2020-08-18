package com.wealoha.social.view.custom;

/**
 * Created by walker on 14-4-19.
 */
public interface InputTextInteract {

    public void insertText(CharSequence text);

    public void appendText(CharSequence text);

    public void backspace();

    public CharSequence getText();

    public void setText(CharSequence text);
}
