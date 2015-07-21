# dump-filter
Simple JAX-RS ContainerFilter to dump the entire response / request to the server.log. This filter is for debugging only and should not be enabled in production systems.

## Installation
Just put the jar to your war. Filter is enabled by default and will log to the INFO level.

## Configuration
You can disable the filter by setting the context-params dump.request / dump.response in your web.xml to false.
