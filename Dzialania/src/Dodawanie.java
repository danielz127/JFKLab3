

import Interfejsy.Description;
import Interfejsy.ICallable;

@Description(description = "Dodawanie dwoch liczb.\nArgumenty: a,b")
public final class Dodawanie implements ICallable {

    @Override
    public int Call(int a, int b) {
        return a + b;
    }


}