Test project to demonstrate EclipseLink bug - looks like Update JPQL does not invalidate Entity caches
===

I've create a profiles to run with different JPA implementations:
 * eclipselink-2.4.2;
 * eclipselink-2.5.0;
 * eclipselink-2.5.1;
 * eclipselink-2.5.2;
 * hibernate-4.2.13;
 * hibernate-4.3.5;
 * openjpa-2.2.2;
 * openjpa-2.3.0;
 * datanucleos-4.0.0;

To run test just execute
mvn -P_profileName_ clean package exec:java

For any JPA provider other than EclipseLink, I got:
```
Field position: 2, as expected.
Field position: 2, as expected.
```
For EclipseLink, I got:
```
Field position: 2, as expected.
Field position: null, expected 2!
```
I've found workarounds: evicting entity from cache or refreshing it after find.
