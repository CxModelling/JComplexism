package factory;

import factory.arguments.AbsArgument;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/11/3.
 */
public class Creator<T> {
    private final String Name;
    private final Class<T> Cls;
    private final AbsArgument[] Args;

    Creator(String name, Class<T> cls, AbsArgument[] args) {
        Name = name;
        Cls = cls;
        Args = args;
    }

    public T create(String name, JSONObject args, Workshop ws) throws InstantiationError{
        List<Class> classes = new ArrayList<>();
        classes.add(String.class);
        List<Object> values = new ArrayList<>();
        values.add(name);
        String key;
        for (AbsArgument arg: Args) {
            key = arg.getName();
            if (args.has(key)) {
                classes.add(arg.getType());
                values.add(arg.correct(args.get(key), ws));
            }
        }
        Class[] classArr = new Class[classes.size()];
        Object[] valueArr = new Object[values.size()];
        for (int i = 0; i < classes.size(); i++) {
            classArr[i] = classes.get(i);
            valueArr[i] = values.get(i);
        }

        try {
            return Cls.getConstructor(classArr).newInstance(valueArr);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new InstantiationError("Object creation failed");
        }
    }

    @Override
    public String toString() {
        return Name + ":" + Cls.getSimpleName() +
                "[" +
                (Arrays.asList(Args)).stream().map(AbsArgument::getName).collect(Collectors.joining(",")) + "]";
    }
}
