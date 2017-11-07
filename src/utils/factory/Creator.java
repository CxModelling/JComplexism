package utils.factory;

import utils.factory.arguments.AbsArgument;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
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
    private final Class Cls;
    private final AbsArgument[] Args;
    private Constructor<T> Con;

    Creator(String name, Class<T> cls, AbsArgument[] args) {
        Name = name;
        Cls = cls;
        Args = args;
        Con = getConstructor();
    }

    private Constructor<T> getConstructor() {
        List<Class> classes = new ArrayList<>();
        classes.add(String.class);

        for (AbsArgument arg: Args) {
            classes.add(arg.getType());
        }
        Class[] classArr = new Class[classes.size()];
        for (int i = 0; i < classes.size(); i++) {
            classArr[i] = classes.get(i);
        }
        // System.out.println(Arrays.toString(Cls.getConstructors()));
        // System.out.println(Arrays.toString(classArr));
        try {
            return Cls.getConstructor(classArr);
        } catch (NoSuchMethodException e) {
            throw new InstantiationError("Constructor did not been identified");
        }
    }

    private Constructor<T> getConstructor(JSONObject args) {
        List<Class> classes = new ArrayList<>();
        classes.add(String.class);
        String key;
        for (AbsArgument arg: Args) {
            key = arg.getName();
            if (args.has(key)) {
                classes.add(arg.getType());
            }
        }
        Class[] classArr = new Class[classes.size()];
        for (int i = 0; i < classes.size(); i++) {
            classArr[i] = classes.get(i);
        }

        try {
            return Cls.getConstructor(classArr);
        } catch (NoSuchMethodException e) {
            throw new InstantiationError("Object creation failed");
        }
    }

    public T create(String name, JSONObject args, Workshop ws) {
        List<Object> values = new ArrayList<>();
        values.add(name);
        String key;
        for (AbsArgument arg: Args) {
            key = arg.getName();
            if (args.has(key)) {
                values.add(arg.correct(args.get(key), ws));
            }
        }

        Object[] valueArr = new Object[values.size()];
        for (int i = 0; i < values.size(); i++) {
            valueArr[i] = values.get(i);
        }

        try {
            return Con.newInstance(valueArr);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InstantiationError("Object creation failed");
        } catch (IllegalArgumentException ignored) {

        }
        try {
            return getConstructor(args).newInstance(valueArr);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e1) {
            throw new InstantiationError("Object creation failed");
        }
    }

    public T create(String name, String[] args, Workshop ws) throws InstantiationError{
        if (args.length != Args.length) {
            throw new InstantiationError("Object creation failed");
        }

        List<Object> values = new ArrayList<>();
        values.add(name);

        for (int i = 0; i < args.length; i++) {
            values.add(Args[i].parse(args[i]));
        }


        Object[] valueArr = new Object[values.size()];
        for (int i = 0; i < valueArr.length; i++) {
            valueArr[i] = values.get(i);
        }

        try {
            return Con.newInstance(valueArr);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
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
