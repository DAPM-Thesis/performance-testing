# performance-testing
A test suite for measuring pipeline performance metrics. At the time of writing, throughput, alignment, and backpressure have been tested.

## Making a Test
To make a test,
1) make appropriate templates in the organizations. We suggest putting the templates in src/main/java/templates/.
2) Add the templates in the organizations' TemplateRepository's. We suggest placing the class in src/main/java/repository/. 
3) Make the pipeline representation (JSON). This will be fetched in the organization's spring boot application. We suggest putting it in src/main/java/representations/.
4) Make config-schemas for the templates 
5) Set up your spring boot application. See OrgA/src/main/java/com/github/dapmthesis/orga/OrgAAplication.java for an example.