# SpamFilterDemo

This code was adapted from another project that demonstrated a Naive Bayes approach to supervised learning which I found here 
on github.  The original example was written in Clojure, my favorite JVM language.  I re-wrote it in Groovy because the firm 
I was consulting for had no specialty in Clojure and really they wanted it in Java, not Groovy.  I found it easier to go from Clojure to Groovy, however.  The version of the code here is sanitized and includes no data or other information related to the client. My interest in hanging on to a version of it is to help others who may need to solve a similar problem.  As the client chose not to continue with a project past the prototype stage, I wanted to salvage whatever value it may hold.

## Introduction

I received a requirement for a prototype system for filtering for the client's web-form-based leads channel.  The business didn't want to implement a captcha or other mechanism for dealing with bots that drop off spam so I came up with a riff on what what Spamassassin does for e-mail but customized it specifically to operate on this client's particular type of data (i.e. similar to email in some ways, but no header data to help score with).  I created a module that would eventually be incorporated into a web service.  This system  would "learn" to filter the spam from the ham by being seeded with representative samples of each type which I hand-scored.  I also later incorporated a training data corpus from Spamassassin.

## Results

My results were mixed.  In the process of hand-scoring the initial training set, I discovered that about 98% of the leads were spam.  I immediately fed that information back to the business to see if they still wanted to proceed.  I was encouraged to continue, so my initial runs with the prototype were giving 70-80% reliability which I felt was too low to be of much use.  That was the point at which I decided to try incorporating more training data from outside, by which I mean the Spamassassin corpus.  That change brought accuracy up quite a bit more, hovering around 85-90%.  I was not only looking at correctly filtered results, but also at false positives and false negatives.  The code includes a mechanism for adjusting weightings such that you can improve overall accuracy.  Over several trials, I was able to dial the system in to around that
90% accuracy figure.  My sense was that a system would probably need something closer to 98% or 99% accuracy to be truly helpful at removing the spam completely automatically, but that this mechanism could be combined with a human reviewer to create a fast process for getting good leads off of a web-form channel.  Continuing to improve the training set might increase accuracy but there is likely a ceiling at which no additional training set data would help at all.

## Conclusion

My spike concluded at the point which I could demonstrate a working system.  The business was impressed, however, they were
stuck on the idea that the filtering needed to be 'per customer' which is how a rather cumbersome existing filtration mechanism worked.  The developers on the team didn't make a hard enough case for applying the machine learning approach.  We had other urgencies as well as constraints on time and personnel resources.  Had I been allowed to presentat my findings directly to the business, perhaps with some charts and examples, they may well have agreed to fund a project to bring a machine learning system into production.  
