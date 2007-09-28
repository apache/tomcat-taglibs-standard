Jakarta Commons Collections classes 
Version: 3.2

These classes are necessary for LRUMap implementation used in ELEvaluator to cache expression strings.

These classes can be deleted for any of the following reasons:
  - a custom caching mechanism is implemented
  - jakarta-commons collections jar file is made a required dependency
  - J2SE 1.4.x or higher is required
    - can leverage LRU LinkedHashmap implementation
