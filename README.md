# population---truth-discovery

This is my current experiments on truth discovery with dataset population.

Data set population is from https://cogcomp.org/page/resource_view/16, source paper is :Knowing What to Believe (when you already know something) 
Jeff Pasternack and Dan Roth
COLING - 2010

I implemented popular truth discovery methods CRH, CATD, GTM, TruthFinder, Sums, Average.log, investment, pooled-investment.

src has codes.
data has files.

First, run PreProcessDataNewYear.java, which preprocesses the data like (A Probabilistic Model for Estimating Real-valued Truth
from Conflicting Sources).
Then, for each algorithm, just run the main function with different parameters. MAE, RMSE, error rate (1%) is reported.
