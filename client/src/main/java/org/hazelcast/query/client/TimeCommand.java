package org.hazelcast.query.client;

import java.text.NumberFormat;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.query.Predicates;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class TimeCommand {

    private final HazelcastInstance client;

    public TimeCommand(HazelcastInstance client) {
        this.client = client;
    }

    @ShellMethod(
            key = {"time-component", "time-comp", "tc"},
            value = "Measure the time it takes to query the Hazelcast analytics map by component's name"
    )
    public String timeByComponentName(
            @ShellOption(
                    help = "The name of the component. For the demo, valid values are first-name, last-name, range, success-button and first-button"
            ) String name
    ) {
        var map = client.getMap("analytics");
        var predicate = Predicates.equal("component", name);
        var start = System.nanoTime();
        map.values(predicate);
        var end = System.nanoTime();
        var formatter = NumberFormat.getInstance();
        return formatter.format(end - start) + " ns";
    }
}
