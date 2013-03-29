<img src="http://weso.es/img/logo_acota_850.png">
# ACOTA:  Automatic Collaborative Tagging 
Master: [![Build Status](https://travis-ci.org/Weso/acota-core.png?branch=master)](https://travis-ci.org/Cesarla/ACOTA)
Develop: [![Build Status](https://travis-ci.org/Weso/acota-core.png?branch=develop)](https://travis-ci.org/Cesarla/ACOTA)


## What is it? ##
ACOTA (Automatic Collaborative Tagging). It is a Java-based library for suggesting 
tags in a collaborative and automatic way. It is based on the use of ontologies to 
manage the tags and provide advanced services of automatic learning, reasoning, etc. 


## Configuration example ##
Acota configuration files only could by written in Java properties (key=value), XML 
comming soon:
Example Coming Soon!

## How to use it? ##

```java
RequestSuggestionTO request = new RequestSuggestionTO();
	
ResourceTO resource = new ResourceTO();
resource.setDescription("WESO is a multidisciplinary research group from the Department of" +
	"Computer Science, Spanish Philology and Philosophy at the University of Oviedo, " +
	"The group is involved in semantic web research, education and technology transfer.");
resource.setLabel("About Web Semantics Oviedo");
resource.setUri("http://www.weso.es");
request.setResource(resource);

EnhancerAdapter luceneEnhancer = new LuceneEnhancer();
EnhancerAdapter openNLPEnhancer = new OpenNLPEnhancer();
EnhancerAdapter wordnetEnhancer = new WordnetEnhancer();
EnhancerAdapter googleEnhancer = new GoogleEnhancer();

luceneEnhancer.setSuccessor(openNLPEnhancer);
openNLPEnhancer.setSuccessor(wordnetEnhancer);
wordnetEnhancer.setSuccessor(googleEnhancer);
googleEnhancer.setSuccessor(labelRecommenderEnhancer);

SuggestionTO suggest = luceneEnhancer.enhance(request);

Map<String, TagTO> labels = suggest.getTags();
```

## Download ##
The current version of acota is **0.3.7-SNAPSHOT**, you can download it from:

### Old Versions
If you need an old version of Acota, please visit the [Acota Downloads Page](https://github.com/Cesarla/ACOTA/wiki/Download---ACOTA)

## Disclaimer
Acota-feedback requires a MySQL Database, you can download the SQL Creation Script from:
 * [ACOTA's Database SQL Dump](http://156.35.82.101:7000/downloads/acota/utils/acota.sql "ACOTA's Database SQL Dump")

Acota does not include Wordnet Dictionary or NLP Files, you can download it from:
 * [Wordnet 3.0](http://wordnetcode.princeton.edu/3.0/WNdb-3.0.tar.gz "Download Wordnet 3.0 Dict Files")
 * [OpenNLP Bundle Files](http://156.35.82.101:7000/downloads/acota/utils/open_nlp.zip "OpenNLP Bundle Files")
 * [OpenNLP English Files](http://156.35.82.101:7000/downloads/acota/utils/open_nlp/es.zip "OpenNLP English Files")
 * [OpenNLP Spanish Files](http://156.35.82.101:7000/downloads/acota/utils/open_nlp/en.zip "OpenNLP Spanish Files")

## License

```
  Copyright 2012-2013 WESO Research Group

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
