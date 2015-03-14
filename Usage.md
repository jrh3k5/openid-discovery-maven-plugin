# Configuring the Plugin #

The following is an example configuration:

```
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>com.googlecode.openid-discovery-maven-plugin</groupId>
                <artifactId>openid-discovery-maven-plugin</artifactId>
                <version>1.0</version>
                <configuration>
                    <serverPort>8777</serverPort>
                    <canonicalIds>
                        <canonicalId>
                            <hostRegex>/discover/canonical/[a-z]+</hostRegex>
                            <canonicalId>http://some.canonicalId</canonicalId>
                        </canonicalId>
                    </canonicalIds>
                    <services>
                        <service>
                            <hostRegex>/discover/[a-z]{3}123</hostRegex>
                            <type>http://specs.openid.net/auth/2.0/server</type>
                            <uri>http://localhost:8080/openid/login</uri>
                        </service>
                        <service>
                            <hostRegex>/openid/user/[0-9]{4}</hostRegex>
                            <type>http://specs.openid.net/auth/2.0/signon</type>
                            <uri>http://localhost:8080/openid/login</uri>
                        </service>
                    </services>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

What this will do is start a server up, listening on port 8777. If you perform OpenID discovery on http://localhost:8777/discover/abc123 or http://localhost:8777/discover/ccc123, you will discover the OpenID endpoint of http://localhost:8080/openid/login.

With this same configuration, if a relying party performs discovery on the OpenID identifier of http://localhost:8777/openid/user/1234, we will resolve the same OP endpoint (http://localhost:8080/openid/login) since the second service configuration is of a signon type.

Additionally, you have the option of sending out discovery documents with canonical IDs. You can see an example of this configuration above; if you visit http://localhost:8777/discovery/canonical/zzz or http://localhost:8777/discovery/canonical/abcdefgh (since these match the configured regular expression), you'll receive an XRDS document with a CanonicalID element whose value is http://some.canonicalId.

# Binding the Plugin to Your Build #

The plugin comes with two goals: start and stop. By default, these bind to pre-integration-test and post-integration-test, respectively. The following configuration shows how to get the server to start and stop before and after integration tests:

```
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>com.googlecode.openid-discovery-maven-plugin</groupId>
                <artifactId>openid-discovery-maven-plugin</artifactId>
                <version>1.0</version>
                <executions>
                    <execution>
                        <id>start-disco-server</id>
                        <goals>
                            <goal>start</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>stop-disco-server</id>
                        <goals>
                            <goal>stop</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

# Running the Discovery Server Standalone #

If you wish to run the discovery server outside of the build lifecycle, you can invoke the following within the working directory of a project whose POM contains a configuration of this project:

```
mvn openid-discovery:run
```

This will start an instance of the server that will block the current thread until you cancel the build.