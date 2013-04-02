<img src="http://weso.es/img/logo_acota_850.png">
# ACOTA:  Automatic Collaborative Tagging 
Master: [![Build Status](https://travis-ci.org/weso/acota-core.png?branch=master)](https://travis-ci.org/weso/acota-core)
Develop: [![Build Status](https://travis-ci.org/weso/acota-core.png?branch=develop)](https://travis-ci.org/weso/acota-core)


## What is it? ##
ACOTA (Automatic Collaborative Tagging). It is a Java-based library for suggesting 
tags in a collaborative and automatic way. It is based on the use of folksonomies to 
manage the tags and provide advanced services of automatic learning, reasoning, etc. 


## Configuration example ##
bAcota allows two different ways to configure it, programmatically and through a properties File:
 - [How to Configure Acota] (https://github.com/weso/acota-core/wiki/Configure-Acota)
 - [Acota-Core (Properties-List)] (https://github.comweso/acota-core/wiki/Acota-Core-(Properties-List\))

## How to use it? ##

Detailed information of how to run acota-core: 
 - [Acota-Core] (https://github.com/weso/acota-core/wiki/Generate-Some-Keywords)

```java
RequestSuggestionTO request = new RequestSuggestionTO();
	
ResourceTO resource = new ResourceTO();
resource.setDescription("WESO is a multidisciplinary research group from the Department of" +
	"Computer Science, Spanish Philology and Philosophy at the University of Oviedo, " +
	"The group is involved in semantic web research, education and technology transfer.");
resource.setLabel("About Web Semantics Oviedo");
resource.setUri("http://www.weso.es");
request.setResource(resource);

EnhancerAdapter tokenizerEnhancer = new TokenizerEnhancer();

SuggestionTO suggest = tokenizerEnhancer.enhance(request);

Map<String, TagTO> labels = suggest.getTags();
```

## Download ##
Grab the latest version of acota-core:
 - [Acota Downloads Page](https://github.com/weso/acota-core/wiki/Download---ACOTA)

## Current Stable Version
The current version of acota is **0.3.6**, you can download it from:
### For Maven Users
 * [acota-core-0.3.6.jar](http://156.35.82.101:7000/downloads/acota/0.3.6/core/acota-core-0.3.6.jar "Download acota-feedback-0.3.6.jar") - [POM File](http://156.35.82.101:7000/downloads/acota/0.3.6/core/acota-core-0.3.6.pom "Download acota-feedback-0.3.6.pom")

 Acota-Core is available in Maven Central:
 ```
  <dependency>
    <groupId>es.weso</groupId>
    <artifactId>acota-core</artifactId>
    <version>0.3.6</version>
 </dependency>
 ```
 * [acota-feedback-0.3.6.jar](http://156.35.82.101:7000/downloads/acota/0.3.6/feedback/acota-feedback-0.3.6.jar "Download acota-feedback-0.3.6.jar") - [POM File](http://156.35.82.101:7000/downloads/acota/0.3.6/feedback/acota-feedback-0.3.6.pom "Download acota-feedback-0.3.6.pom")

### For Non Maven Users
Acota-bundle includes all required dependancies:

 * [acota-bundle-0.3.6.jar](http://156.35.82.101:7000/downloads/acota/0.3.6/bundle/acota-bundle-0.3.6.jar "Download acota-bundle-0.3.6.jar")


### Old Versions
If you need an old/snapshot version of acota-core, please visit the [Acota Downloads Page](https://github.com/weso/acota-core/wiki/Download---ACOTA)

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
