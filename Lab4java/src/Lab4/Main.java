package Lab4;

public class Main {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor() {


            @Override
            public void associateWithSpellChecker(SpellCheck checker) {

            }
        };
        SpellCheck checker = new SpellCheck();

        editor.associateWithSpellCheck(checker);
        editor.display();
        editor.performEdit();

        Filemanager manager = new Filemanager();
        manager.save(editor.getContent());
    }
}