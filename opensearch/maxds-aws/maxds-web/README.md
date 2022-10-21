### Steps to setup, build, copy, run and test
	install node.js from (https://nodejs.org/en/) if you dont have node js installed on your system.
	
	npm config set registry http://registry.npmjs.org/
	
	npm install typescript@3.1.1

	npm install (This will create a folder 'node_modules' in the root of the project with all libraries)
	
	npm run start to start web application on http://localhost:4200/

### To install any new libraries. Do the following( ex:primeng)
	npm install primeng --save-dev --> This will install the library and save it in package.json as devDependencies

### copy files to idap-em-app from dist folder of idap-em-cf
	Run `npm run build` to build the project (it will create static folder at the root of maxds-app\src\main\resources folder)
	
	after above step
	goto maxds-app
	clean compile package
	java -jar  target/maxds-app-3.1.0-SNAPSHOT.war
	http://localhost:18086/welcome, click on Entities link to see updates)

### Do not checkin 'node_modules' and 'dist' folders to svn. 

# IdapEmWebCf
This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 1.1.3.

## Development server
Run `npm run start` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding
Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|module`.

## Build
Run `npm run build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `-prod` flag for a production build.

## Running unit tests
Run `npm run test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests
Run `npm run e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).
Before running the tests make sure you are serving the app via `ng serve`.

## Running idap-em-app in InternetExplorer
/** IE9, IE10 and IE11 requires all of the following polyfills. **/
Enable all the polyfills in /trunk/misc-projects/MAXDS-WEB/src/polyfills.ts

## Further help
To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).



## Upgrade to latest angular version can be done by following steps
1) Upgrade the Angular version globally by adding the latest version via terminal:  

npm install -g @angular/cli@latest

2) Upgrade the version locally in your project and make sure the changes for the new version are reflected in the package.json file:

ng update @angular/cli

3) Upgrade all your dependencies and dev dependencies in package.json 

i) Dependencies:

npm install --save @angular/animations@latest @angular/cdk@latest @angular/common@latest @angular/compiler@latest @angular/core@latest @angular/flex-layout@latest @angular/forms@latest @angular/http@latest @angular/material@latest @angular/platform-browser@latest @angular/platform-browser-dynamic@latest @angular/router@latest core-js@latest zone.js@latest rxjs@latest rxjs-compat@latest

ii) Dev Dependencies:

npm install --save-dev @angular-devkit/build-angular@latest @angular/compiler-cli@latest @angular/language-service @types/jasmine@latest @types/node@latest codelyzer@latest karma@latest karma-chrome-launcher@latest karma-cli@latest karma-jasmine@latest karma-jasmine-html-reporter@latest jasmine-core@latest jasmine-spec-reporter@latest protractor@latest tslint@latest rxjs-tslint@latest webpack@latest

4) Angular-devkit was introduced in Angular 6 to build Angular applications that required dependency on your CLI projects.

npm install @angular-devkit/build-angular@latest

5) Upgrade the version for Typescript

npm install typescript@latest --save-dev

6) Migrate the configuration of angular-cli.json to angular.json

ng update @angular/cli
ng update @angular/core

7) If Angular material is used, use this command:

ng update @angular/material

8) Remove deprecated RxJS 6 features by running below commands

i) npm install -g rxjs-tslint
ii) rxjs-5-to-6-migrate -p src/tsconfig.app.json

9) uninstall rxjs-compat as it is an unnecessary dependency for Angular 7.

npm uninstall --save rxjs-compat

10)  changeimport { Observable } from 'rxjs/Observable'; to import { Observable } from 'rxjs';

11) As MAXDS-Web is based on primng framework need to install dependencies as required. Run below commands.

i) npm install primeng --save
ii) npm install primeicons --save

12) Add PrimeNG and PrimeIcons as a dependencies. If not added in package.json.
"dependencies": {
  //...
  "primeng": "^7.0.0",
  "primeicons": "^1.0.0"
}

13) Add below styles in angular.json file they are not added.

"styles": [
  "node_modules/primeng/resources/themes/nova-light/theme.css",
  "node_modules/primeng/resources/primeng.min.css",
  "node_modules/primeicons/primeicons.css",
  //...
],