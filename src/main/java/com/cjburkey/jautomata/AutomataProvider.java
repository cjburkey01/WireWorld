package com.cjburkey.jautomata;

/**
 * Created by CJ Burkey on 2018/11/26
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AutomataProvider {
    
    private JAutomata automata;
    
    protected static void start(AutomataProvider instance, String[] args) {
        JAutomata.boot(instance, args);
    }
    
    protected void init() {
    }
    
    protected final void exit() {
        if (automata != null) automata.exit();
    }
    
    public abstract IAutomataHandler getHandler();
    
    // Returns false to allow default input handling
    public boolean handleExtraInput(Input input) {
        return false;
    }
    
    final void setAutomata(JAutomata automata) {
        this.automata = automata;
    }
    
    public final JAutomata getAutomata() {
        return automata;
    }
    
}
