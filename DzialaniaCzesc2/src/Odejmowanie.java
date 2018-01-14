import Interfejsy.Description;
import Interfejsy.ICallable;

@Description(description = "Odejmowanie dwoch liczb.\nArgumenty: a,b")
public final class Odejmowanie implements ICallable {

    @Override
    public int Call(int a, int b) {
        return a - b;
    }

}