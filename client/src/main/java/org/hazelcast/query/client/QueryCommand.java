package org.hazelcast.query.client;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.query.Predicates;
import org.json.JSONObject;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

@ShellComponent
public class QueryCommand {

    private static final String[] FIELDS = new String[] { "component", "type", "x", "y", "event", "timestamp", "instant", "session" };

    private final HazelcastInstance client;

    public QueryCommand(HazelcastInstance client) {
        this.client = client;
    }

    @ShellMethod(
            key = { "query-component", "query", "q" },
            value = "Query the Hazelcast analytics map by component's name"
    )
    public Table queryByComponentName(
            @ShellOption(
                    help = "The name of the component. For the demo, valid values are first-name, last-name, range, success-button and first-button"
            ) String name
    ) {
        var modelBuilder = new TableModelBuilder<>();
        var map = client.getMap("analytics");
        var entry = Predicates.newPredicateBuilder().getEntryObject();
        var predicate = entry.get("component").equal(name);
        Collection<HazelcastJsonValue> values = map.values(predicate);
        modelBuilder.addRow();
        Stream.of(FIELDS).forEach(modelBuilder::addValue);
        values.stream()
                .map(HazelcastJsonValue::toString)
                .map(JSONObject::new)
                .forEach(json -> {
                    modelBuilder.addRow();
                    Stream.of(FIELDS).forEach(field -> new ModelEnricher(field).accept(modelBuilder, json));
                });
        var builder = new TableBuilder(modelBuilder.build());
        builder.addFullBorder(BorderStyle.oldschool);
        return builder.build();
    }

    private static class ModelEnricher implements BiConsumer<TableModelBuilder<Object>, JSONObject> {

        private final String fieldName;

        private ModelEnricher(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public void accept(TableModelBuilder<Object> modelBuilder, JSONObject json) {
            if (json.has(fieldName)) {
                modelBuilder.addValue(json.get(fieldName));
            } else {
                modelBuilder.addValue(null);
            }
        }
    }
}
