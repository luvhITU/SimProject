import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.world.NonBlocking;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Hole extends SimComponent implements NonBlocking, DynamicDisplayInformationProvider {
    List<Hole> exits;

    public Hole() {
        this.exits = new ArrayList<>();
    }

    // TODO: Max 2-10 (5) kaniner pr. hul.

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.black, "hole");
    }

}
