package hgm.validators;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by TimeWz on 2017/8/11.
 */
public class FileValidator implements IValidator<String> {
    public String FilenameExtension;

    public FileValidator(String ext) {
        FilenameExtension = ext;
    }

    public FileValidator() {
        this(null);
    }

    @Override
    public void check(Object val) throws ValidationError {
        String path = val.toString();
        File file = new File(path);
        if (!file.exists()) {
            throw new ValidationError("Invalidate path");
        }

        Matcher m = Pattern.compile(".*/.*?(\\..*)").matcher(path);
        if (m.matches()) {
            if (FilenameExtension != null &
                    !m.group(1).equals(FilenameExtension)) {
                throw new ValidationError("Unmatched filename extension");
            }
        } else {
            throw new ValidationError("Invalidate filename");
        }
    }

    @Override
    public String adjust(Object val) throws ValidationError {
        check(val);
        return (String) val;

    }

    @Override
    public String getDefault() {
        return null;
    }
}
