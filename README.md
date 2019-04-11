# population---truth-discovery

This is my current experiments on truth discovery with dataset population.

Data set population is from https://cogcomp.org/page/resource_view/16, source paper is :Knowing What to Believe (when you already know something), Jeff Pasternack and Dan Roth, COLING - 2010

I implemented popular truth discovery methods CRH, CATD, GTM, TruthFinder, Sums, Average.log, investment, pooled-investment.

src has java codes.
data has files.

First, run PreProcessDataNewYear.java, which preprocesses the data like (A Probabilistic Model for Estimating Real-valued Truth from Conflicting Sources).
Then, for each algorithm, just run the main function with different parameters. MAE, RMSE, error rate (1%) is reported.

After preprocessing, 4183 claims about 1172 population entities from 2415 sources. 277 city-year pairs appear in evaluation.
Compared with "4119 claims about 1148 population entities from 2415 sources. 274 city-year pairs" in (A Probabilistic Model for Estimating Real-valued Truth from Conflicting Sources).


	MAE	RMSE	Error Rate(>1%)				
GTM	2438.34691	8771.48494	0.386281588	1	1	0	1

TruthFinder	1753.696751	8944.302197	0.158844765	0.2		

GTM	2713.853822	9162.901737	0.436823105	10	10	0	1

Sums	2223.953069	9669.474524	0.187725632

median	2475.046931	9759.706267	0.296028881

mean	3336.490927	9799.66196	0.505415162

CRH	3533.122935	10770.55118	0.494584838

voting	2511.043321	11328.70554	0.209386282

CATD	2789.736356	11970.49794	0.270758123




			
