package fitter;

import pcore.Gene;

import java.util.List;

/**
 *
 * Created by TimeWz on 2017/4/25.
 */
public interface IFitter {
    List<Gene> getPosterior();

    void fit(int niter);

}
