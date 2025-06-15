package Lab4;

import Lab4.interfaces.Editable;
import Lab4.interfaces.SpellCheckable;

public abstract class TextEditor implements Editable, SpellCheckable {

    private StringBuilder content;
    private final TextBuffer buffer;

    public TextEditor() {
        this.buffer = new TextBuffer();
        this.content = new StringBuilder();
    }

    public void insertText(String text) {
        buffer.add(text);
        content.append(text);
    }

    public void deleteLastLine() {
        int lastIndex = content.lastIndexOf("\n");
        if (lastIndex != -1) {
            content.delete(lastIndex, content.length());
        } else {
            content.setLength(0);
        }
    }

    public void replaceText(String target, String replacement) {
        int index;
        while ((index = content.indexOf(target)) != -1) {
            content.replace(index, index + target.length(), replacement);
        }
    }

    public boolean search(String word) {
        return content.toString().contains(word);
    }

    public void display() {
        System.out.println("Content:\n" + content);
    }

    public void performEdit() {
        class UndoAction {
            void undo() {
                System.out.println("Undo last action...");
            }
        }
        UndoAction action = new UndoAction();
        action.undo();
    }

    public void associateWithSpellCheck(SpellCheck checker) {
        checker.check(content.toString());
    }

    public String getContent() {
        return content.toString();
    }

    public void associateWithSpellcheck(SpellCheck checker) {

    }
    }
