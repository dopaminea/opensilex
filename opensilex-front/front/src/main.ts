/// <reference path="../../../opensilex/front/types/opensilex.d.ts" />
import { SecurityService } from 'opensilex/index'
import "reflect-metadata"
import Vue from 'vue'
import App from './App.vue'
import { FrontConfigDTO, FrontService } from './lib'
import { OpenSilexVuePlugin } from './plugin/OpenSilexVuePlugin'
import store from './store'

// Initialize cookie management library
import VueCookies from 'vue-cookies'
Vue.use(VueCookies);

// Load default components
import components from './components';
for (let componentName in components) {
  Vue.component(componentName, components[componentName]);
}

// Initialise bootstrap
import BootstrapVue from 'bootstrap-vue'
Vue.use(BootstrapVue);

// Initialise font awesome
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faPowerOff } from '@fortawesome/free-solid-svg-icons'
library.add(faPowerOff)
Vue.component('font-awesome-icon', FontAwesomeIcon)

// Import and assignation to enable auto rebuild on ws library change
import * as LATEST_UPDATE from "./opensilex.dev";
import HttpResponse from 'opensilex/HttpResponse'
import { ModuleComponentDefinition } from './plugin/ModuleComponentDefinition'
import { User } from './users/User'
Vue.prototype.LATEST_UPDATE = LATEST_UPDATE.default

// Allow access to global "document" variable
declare var document: any;

// Default Vue configuration
Vue.config.productionTip = false

// Initialize service API container
const DEV_BASE_API_PATH = "http://localhost:8666/rest";

let baseApi = DEV_BASE_API_PATH;
if (window["webpackHotUpdate"]) {
  console.warn(
    'Vue is running in development mode, with base API set by default to ' + DEV_BASE_API_PATH + '\n' +
    'If you start your webservices server with another host or port configuration,\n' +
    'please edit opensilex-front/front/src/main.ts and update DEV_BASE_API_PATH constant'
  );
} else {
  let splitURI = window.location.href.split("/");
  baseApi = splitURI[0] + "//" + splitURI[2] + "/rest/"
}

// Enable Vue front plugin manager for OpenSilex API
let frontPlugin = new OpenSilexVuePlugin(baseApi);
Vue.use(frontPlugin);

// Define global error manager
const manageError = function manageError(error) {
  console.error(error);
  document.getElementById('opensilex-error-loading').style.visibility = 'visible';
}

// Get OpenSilex configuration
const frontService = frontPlugin.getService<FrontService>("FrontService");
frontService.getConfig()
  .then(function (configResponse: HttpResponse<FrontConfigDTO>) {
    const config: FrontConfigDTO = configResponse.response;

    // Initalise main layout components from configuration
    let footerDefinition: ModuleComponentDefinition = ModuleComponentDefinition.fromString(config.footerComponent);
    let headerDefinition: ModuleComponentDefinition = ModuleComponentDefinition.fromString(config.headerComponent);
    let homeDefinition: ModuleComponentDefinition = ModuleComponentDefinition.fromString(config.homeComponent);
    let loginDefinition: ModuleComponentDefinition = ModuleComponentDefinition.fromString(config.loginComponent);
    let menuDefinition: ModuleComponentDefinition = ModuleComponentDefinition.fromString(config.menuComponent);
    let notFoundDefinition: ModuleComponentDefinition = ModuleComponentDefinition.fromString(config.notFoundComponent);
    
    store.commit("setConfig", config);

    frontPlugin.loadComponentModules([
      footerDefinition,
      headerDefinition,
      homeDefinition,
      loginDefinition,
      menuDefinition,
      notFoundDefinition
    ]).then(function () {
      // Check user login
      let user: User = User.fromCookie();
      store.commit("login", user);

      // Init routing
      let router = store.state.router;

      new Vue({
        router,
        store,
        render: h => h(App, {
          props: {
            footerComponentDef: footerDefinition,
            headerComponentDef: headerDefinition,
            loginComponentDef: loginDefinition,
            menuComponentDef: menuDefinition
          }

        },
        )
      }).$mount('#app')

      document.getElementById('opensilex-loader').style.visibility = 'hidden';
    }).catch(manageError);
  })
  .catch(manageError);
