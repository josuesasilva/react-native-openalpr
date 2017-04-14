## React Native OpenALPR
This project wraps [OpenALPR Android](https://github.com/SandroMachado/openalpr-android) library.

## Installing it as a library in your main project
There are many ways to do this, here's the way I do it:

1. Push it to **GitHub**.
2. Do `npm install --save git+https://github.com/josuesasilva/react-native-openalpr.git` in your main project.
3. Link the library:
    * Add the following to `android/settings.gradle`:
        ```
        include ':react-native-openalpr'
        project(':react-native-openalpr').projectDir = new File(settingsDir, '../node_modules/react-native-openalpr/android')
        ```

    * Add the following to `android/app/build.gradle`:
        ```xml
        ...

        dependencies {
            ...
            compile project(':react-native-openalpr')
        }
        ```
    * Add the following to `android/app/src/main/java/**/MainApplication.java`:
        ```java
        import com.reactnative.openalpr.Package;  // add this for react-native-openalpr

        public class MainApplication extends Application implements ReactApplication {

            @Override
            protected List<ReactPackage> getPackages() {
                return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new OpenALPRPackage()     // add this for react-native-openalpr
                );
            }
        }
        ```
4. Simply `import` it by the name defined in your library's `package.json`:

    ```javascript
    import OpenALPR from 'react-native-openalpr'

		OpenALPR.start((result) => {
			JSON.parse(result);
		},
		(error) => {
			JSON.parse(error);
		});