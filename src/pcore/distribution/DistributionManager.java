package pcore.distribution;

import utils.factory.Workshop;
import utils.factory.arguments.AbsArgument;
import utils.factory.arguments.DoubleArg;

import java.text.DecimalFormat;


/**
 * todo check variable orders
 * Created by TimeWz on 2017/4/17.
 */
public class DistributionManager {
    private static DecimalFormat df = new DecimalFormat("0.0");
    private static Workshop Distributions = new Workshop();

    static {
        df.setMaximumFractionDigits(6);

        AbsArgument[] ags;

        ags = new AbsArgument[]{new DoubleArg("rate")};
        Distributions.register("exp", Exponential.class, ags);

        ags = new AbsArgument[]{new DoubleArg("k")};
        Distributions.register("k", Const.class, ags);

        ags = new AbsArgument[]{new DoubleArg("df")};
        Distributions.register("chisq", Chi2.class, ags);

        ags = new AbsArgument[]{new DoubleArg("df")};
        Distributions.register("t", T.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("a"),
                new DoubleArg("m"),
                new DoubleArg("b")};
        Distributions.register("triangle", Triangle.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("alpha"),
                new DoubleArg("beta")};
        Distributions.register("weibull", Weibull.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("mean"),
                new DoubleArg("sd")};
        Distributions.register("norm", Normal.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("meanlog"),
                new DoubleArg("sdlog")};
        Distributions.register("lnorm", Lognormal.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("min"),
                new DoubleArg("max")};
        Distributions.register("unif", Uniform.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("shape"),
                new DoubleArg("rate")};
        Distributions.register("gamma", Gamma.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("shape1"),
                new DoubleArg("shape2")};
        Distributions.register("beta", Beta.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("lambda")};
        Distributions.register("pois", Poisson.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("size"),
                new DoubleArg("prob")};
        Distributions.register("binom", Beta.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("lambda")};
        Distributions.register("geom", Geometric.class, ags);
    }


    public static IDistribution parseDistribution(String input) {
        String code = input.replaceAll("\\s+", "");
        code = code.replaceAll("(\\(|\\))", " ");
        String[] mat = code.split(" ");

        String[] args = mat[1].split(",");
        return (IDistribution) Distributions.create(input, mat[0], args);
    }

}
