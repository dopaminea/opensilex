<template>
  <b-modal ref="modalRef" @ok.prevent="validate" size="xl" :static="true">
    <template v-slot:modal-ok>{{$t('component.common.ok')}}</template>
    <template v-slot:modal-cancel>{{$t('component.common.cancel')}}</template>

    <template v-slot:modal-title>{{title}}</template>
    <ValidationObserver ref="validatorRef">
      <b-form>
        <!-- URI -->
        <b-form-group>
          <ValidationProvider vid="autogeneratedInfra">
            <b-form-checkbox
              v-if="!editMode"
              id="autogeneratedInfra"
              v-model="uriGenerated"
              name="autogeneratedInfra"
            >
              <opensilex-FormInputLabelHelper
                label="component.infrastructure.infrastructure-uri"
                helpMessage="component.common.uri.help-message"
                labelFor="uriinfra"
              ></opensilex-FormInputLabelHelper>
            </b-form-checkbox>
          </ValidationProvider>
          <ValidationProvider
            name="uriinfra"
            rules="required_if:autogeneratedInfra,false|url"
            v-slot="{ errors }"
          >
            <b-form-input
              id="uriinfra"
              v-model="form.uri"
              :disabled="uriGenerated"
              type="text"
              required
              :placeholder="$t('component.common.autogenerated-uri')"
            ></b-form-input>
            <div class="error-message alert alert-danger">{{ errors[0] }}</div>
          </ValidationProvider>
        </b-form-group>
        <!-- Name -->
        <b-form-group :label="$t('component.common.name') + ':'" label-for="name" required>
          <ValidationProvider
            :name="$t('component.common.name')"
            rules="required"
            v-slot="{ errors }"
          >
            <b-form-input
              id="name"
              v-model="form.name"
              type="text"
              required
              :placeholder="$t('component.infrastructure.form-name-placeholder')"
            ></b-form-input>
            <div class="error-message alert alert-danger">{{ errors[0] }}</div>
          </ValidationProvider>
        </b-form-group>
        <!-- Type -->
        <b-form-group :label="$t('component.common.type') + ':'" label-for="type" required>
          <ValidationProvider
            :name="$t('component.common.type')"
            rules="required"
            v-slot="{ errors }"
          >
            <treeselect
              id="type"
              :options="infraTypesOptions"
              :load-options="initInfraTypes"
              :placeholder="$t('component.infrastructure.form-type-placeholder')"
              v-model="form.type"
            />
            <div class="error-message alert alert-danger">{{ errors[0] }}</div>
          </ValidationProvider>
        </b-form-group>
        <!-- Parent -->
        <b-form-group :label="$t('component.common.parent') + ':'" label-for="parent">
          <treeselect
            id="parent"
            :options="parentOptions"
            :placeholder="$t('component.infrastructure.form-parent-placeholder')"
            v-model="form.parent"
          />
        </b-form-group>
      </b-form>
    </ValidationObserver>
  </b-modal>
</template>

<script lang="ts">
import { Component, Prop, Ref } from "vue-property-decorator";
import Vue from "vue";
import VueRouter from "vue-router";
import HttpResponse, { OpenSilexResponse } from "../../lib/HttpResponse";
import Oeso from "../../ontologies/Oeso";
import {
  OntologyService,
  InfrastructureGetDTO,
  InfrastructureCreationDTO,
  ResourceTreeDTO
} from "opensilex-core/index";

@Component
export default class InfrastructureForm extends Vue {
  $opensilex: any;
  $store: any;
  $router: VueRouter;
  $i18n: any;
  $t: any;
  service: OntologyService;

  @Prop()
  public defaultParent: InfrastructureGetDTO;

  @Prop()
  public parentOptions: Array<any>;

  @Ref("modalRef") readonly modalRef!: any;

  @Ref("validatorRef") readonly validatorRef!: any;

  get user() {
    return this.$store.state.user;
  }

  uriGenerated = true;

  form: InfrastructureCreationDTO = {
    uri: "",
    type: null,
    name: "",
    parent: null
  };

  infraTypesOptions = null;

  title = "";

  editMode = false;

  created() {
    this.service = this.$opensilex.getService(
      "opensilex-security.OntologyService"
    );
  }

  mounted() {
    this.$store.watch(
      () => this.$store.getters.language,
      lang => {
        this.loadInfraTypes();
      }
    );
  }

  initInfraTypes({ action, parentNode, callback }) {
    this.loadInfraTypes(callback);
  }

  loadInfraTypes(callback?) {
    this.service
      .getSubClassesOf(Oeso.INFRASTRUCTURE_TYPE_URI, true)
      .then((http: HttpResponse<OpenSilexResponse<Array<ResourceTreeDTO>>>) => {
        this.infraTypesOptions = this.$opensilex.buildTreeListOptions(
          http.response.result
        );
        if (callback) {
          callback();
        }
      })
      .catch(this.$opensilex.errorHandler);
  }

  clearForm() {
    let parentURI = null;
    if (this.defaultParent) {
      parentURI = this.defaultParent.uri;
    }
    this.form = {
      uri: "",
      type: null,
      name: "",
      parent: parentURI
    };
  }

  showCreateForm(parentURI) {
    this.clearForm();
    this.form.parent = parentURI;
    this.editMode = false;
    this.title = this.$t("component.infrastructure.add").toString();
    this.uriGenerated = true;
    this.$opensilex.filterItemTree(this.parentOptions, this.form.uri);
    this.validatorRef.reset();
    this.modalRef.show();
  }

  showEditForm(form: InfrastructureCreationDTO) {
    this.form = form;
    this.editMode = true;
    this.title = this.$t("component.infrastructure.update").toString();
    this.uriGenerated = true;
    this.$opensilex.filterItemTree(this.parentOptions, this.form.uri);
    let modalRef: any = this.modalRef;
    this.validatorRef.reset();
    modalRef.show();
  }

  hideForm() {
    let modalRef: any = this.modalRef;
    modalRef.hide();
  }

  onValidate() {
    return new Promise((resolve, reject) => {
      if (this.editMode) {
        this.$emit("onUpdate", this.form, result => {
          if (result instanceof Promise) {
            result.then(resolve).catch(reject);
          } else {
            resolve(result);
          }
        });
      } else {
        return this.$emit("onCreate", this.form, result => {
          if (result instanceof Promise) {
            result.then(resolve).catch(reject);
          } else {
            resolve(result);
          }
        });
      }
    });
  }

  validate() {
    let validatorRef: any = this.validatorRef;
    validatorRef.validate().then(isValid => {
      if (isValid) {
        if (this.uriGenerated && !this.editMode) {
          this.form.uri = null;
        }

        this.onValidate()
          .then(() => {
            this.$nextTick(() => {
              let modalRef: any = this.modalRef;
              modalRef.hide();
            });
          })
          .catch(error => {
            if (error.status == 409) {
              console.error("Infrastructure already exists", error);
              this.$opensilex.errorHandler(
                error,
                this.$i18n.t(
                  "component.infrastructure.errors.infrastructure-already-exists"
                )
              );
            } else {
              this.$opensilex.errorHandler(error);
            }
          });
      }
    });
  }
}
</script>

<style scoped lang="scss">
</style>

