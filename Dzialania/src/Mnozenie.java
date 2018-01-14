
import Interfejsy.Description;
import Interfejsy.ICallable;

@Description(description = "Mnozenie dwoch liczb.\nArgumenty: a,b")
public final class Mnozenie implements ICallable {

    @Override
    public int Call(int a, int b) {
        return a * b;
    }


}