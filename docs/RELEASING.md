# Releasing

The BlueMap 5.23 migration remains unpublished until its exact candidate has
passed the combined integration gallery and owner review.

After the owner accepts the candidate:

1. Confirm the accepted bounded gallery fixture.
2. Build production JAR, sources JAR, POM, and Gradle module metadata with the
   exact promotion Java/Gradle/BlueMap inputs.
3. Put their exact sizes and SHA-256 values in `gradle.properties` and complete
   `provenance/release.json`.
4. Set the provenance status to `owner-accepted-release-candidate` and run
   `verifyReleaseCandidate -PreleaseTag=v<version>` with all exact candidate
   JAR Gradle properties.
5. Merge the reviewed commit, create an annotated `v<version>` tag at that
   commit, and let `.github/workflows/release.yml` publish.
6. Compare every downloaded release asset to the locally accepted bytes.
7. Update the private root portfolio, queue, and `workspace.json` in a separate
   orchestration commit.

The tag must exactly equal `v<addon_version>`. No release authorizes production
deployment.

The command sequence and required release-provenance fields are recorded in
[`EXECUTION.md`](EXECUTION.md).
