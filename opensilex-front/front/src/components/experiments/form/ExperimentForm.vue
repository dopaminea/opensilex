<template>
  <div ref="modalRef" @ok.prevent="validate">
    <ValidationObserver ref="validatorRef">
      <b-form>
        <!-- URI -->
        <b-form-group>
          <opensilex-FormInputLabelHelper
            label="component.experiment.uri"
            helpMessage="component.experiment.uri-help"
          ></opensilex-FormInputLabelHelper>
          <ValidationProvider vid="autogenerated">
            <b-form-checkbox
              v-if="!editMode"
              id="autogenerated"
              v-model="uriGenerated"
              name="autogenerated"
            >{{$t('component.common.autogenerated-uri')}}</b-form-checkbox>
          </ValidationProvider>
          <ValidationProvider
            name="uri"
            rules="required_if:autogenerated,false|url"
            v-slot="{ errors }"
          >
            <b-form-input
              id="uri"
              v-model="form.uri"
              :disabled="uriGenerated"
              type="text"
              required
              :placeholder="$t('component.common.autogenerated-uri')"
            ></b-form-input>
            <div class="error-message alert alert-danger">{{ errors[0] }}</div>
          </ValidationProvider>
        </b-form-group>

        <!-- Label -->
        <b-form-group required>
          <opensilex-FormInputLabelHelper
            label="component.experiment.label"
            helpMessage="component.experiment.label-help"
          ></opensilex-FormInputLabelHelper>
          <ValidationProvider
            :name="$t('component.experiment.label')"
            rules="required"
            v-slot="{ errors }"
          >
            <b-form-input
              id="label"
              v-model="form.label"
              type="text"
              required
              :placeholder="$t('component.experiment.label-placeholder')"
            ></b-form-input>
            <div class="error-message alert alert-danger">{{ errors[0] }}</div>
          </ValidationProvider>
        </b-form-group>

        <div class="row">
          <!-- StartDate -->
          <div class="col-lg-6">
            <b-form-group
              required
              :label="$t('component.experiment.startDate') + ':'"
              label-for="startDate"
            >
              <ValidationProvider
                :name="$t('component.experiment.startDate')"
                rules="required"
                v-slot="{ errors }"
              >
                <b-form-input
                  id="startDate"
                  v-model="form.startDate"
                  type="date"
                  value="2020-03-05"
                  required
                ></b-form-input>
                <div class="error-message alert alert-danger">{{ errors[0] }}</div>
              </ValidationProvider>
            </b-form-group>
          </div>
          <!-- EndDate -->
          <div class="col-lg-6">
            <b-form-group :label="$t('component.experiment.endDate') + ':'" label-for="endDate">
              <ValidationProvider :name="$t('component.experiment.endDate')" v-slot="{ errors }">
                <b-form-input id="endDate" v-model="form.endDate" type="date" value="2020-03-05"></b-form-input>
                <div class="error-message alert alert-danger">{{ errors[0] }}</div>
              </ValidationProvider>
            </b-form-group>
          </div>
        </div>

        <!-- Species -->
        <b-form-group required>
          <opensilex-FormInputLabelHelper
            label="component.experiment.species"
            helpMessage="component.experiment.species-help"
          ></opensilex-FormInputLabelHelper>
          <ValidationProvider :name="$t('component.experiment.species')" v-slot="{ errors }">
            <b-form-select id="speciesList" v-model="form.species" :options="speciesList"></b-form-select>
            <div class="error-message alert alert-danger">{{ errors[0] }}</div>
          </ValidationProvider>
        </b-form-group>

        <!-- Objective -->
        <b-form-group required>
          <opensilex-FormInputLabelHelper
            label="component.experiment.objective"
            helpMessage="component.experiment.objective-help"
          ></opensilex-FormInputLabelHelper>
          <ValidationProvider :name="$t('component.experiment.objective')" v-slot="{ errors }">
            <b-form-textarea
              id="objective"
              v-model="form.objective"
              type="textarea"
              :placeholder="$t('component.experiment.objective-placeholder')"
            ></b-form-textarea>
            <div class="error-message alert alert-danger">{{ errors[0] }}</div>
          </ValidationProvider>
        </b-form-group>

        <!-- Comment -->
        <b-form-group required>
          <opensilex-FormInputLabelHelper
            label="component.experiment.comment"
            helpMessage="component.experiment.comment-help"
          ></opensilex-FormInputLabelHelper>
          <ValidationProvider :name="$t('component.experiment.comment')" v-slot="{ errors }">
            <b-form-textarea
              id="comment"
              v-model="form.comment"
              type="textarea"
              :placeholder="$t('component.experiment.comment-placeholder')"
            ></b-form-textarea>
            <div class="error-message alert alert-danger">{{ errors[0] }}</div>
          </ValidationProvider>
        </b-form-group>

        <!-- Campaign -->
        <b-form-group required>
          <opensilex-FormInputLabelHelper
            label="component.experiment.campaign"
            helpMessage="component.experiment.campaign-help"
          ></opensilex-FormInputLabelHelper>
          <ValidationProvider :name="$t('component.experiment.campaign')" v-slot="{ errors }">
            <b-form-input
              id="campaign"
              v-model="form.campaign"
              type="number "
              :placeholder="$t('component.experiment.campaign-placeholder')"
            ></b-form-input>
            <div class="error-message alert alert-danger">{{ errors[0] }}</div>
          </ValidationProvider>
        </b-form-group>

        <!-- Keywords -->
        <!-- <b-form-group  required  >
          <opensilex-FormInputLabelHelper 
          label=component.experiment.keywords 
          helpMessage="component.experiment.keywords-help" >
          </opensilex-FormInputLabelHelper>
           <ValidationProvider :name="$t('component.experiment.keywords')" v-slot="{ errors }">
            <b-form-input  id="keywords"  v-model="keywords"  type="text"
              :placeholder="$t('component.experiment.keywords-placeholder')" >
            </b-form-input>
            <div class="error-message alert alert-danger">{{ errors[0] }}</div>
          </ValidationProvider>
        </b-form-group>-->
      </b-form>
    </ValidationObserver>
  </div>
</template>

<script lang="ts">
import { Component, Prop } from "vue-property-decorator";
import Vue from "vue";
import VueRouter from "vue-router";

import {
  ExperimentCreationDTO,
  SpeciesService,
  SpeciesDTO
} from "opensilex-core/index";
import HttpResponse, { OpenSilexResponse } from "opensilex-core/HttpResponse";

@Component
export default class ExperimentForm extends Vue {
  $opensilex: any;
  $store: any;
  $router: VueRouter;
  $i18n: any;

  title = "";
  editMode = false;
  uriGenerated = true;

  keywords: string;
  speciesList: any = [];

  get user() {
    return this.$store.state.user;
  }

  form: ExperimentCreationDTO = {
    projects: [],
    keywords: [],
    scientificSupervisors: [],
    technicalSupervisors: [],
    groups: [],
    infrastructures: [],
    installations: [],
    variables: [],
    sensors: []
    // lang: "en-US"
  };

  clearForm() {
    this.form = {
      uri: null,
      label: null,
      projects: [],
      startDate: null,
      endDate: null,
      objective: null,
      comment: null,
      keywords: [],
      scientificSupervisors: [],
      technicalSupervisors: [],
      groups: [],
      infrastructures: [],
      installations: [],
      species: null,
      isPublic: null,
      variables: [],
      sensors: []
      // lang: "en-US"
    };
    this.keywords = null;
  }

  getForm(): ExperimentCreationDTO {
    return this.form;
  }

  getKeywords(): string {
    return this.keywords;
  }

  created() {
    this.loadSpecies();

    if (this.$store.editXp !== undefined) {
      this.editMode = this.$store.editXp;
      if (this.editMode && this.$store.xpToUpdate != undefined) {
        let dto = this.$store.xpToUpdate;
        this.form = dto;
      }
    }
  }

  loadSpecies() {
    let service: SpeciesService = this.$opensilex.getService(
      "opensilex.SpeciesService"
    );
    service
      .getAllSpecies()
      .then((http: HttpResponse<OpenSilexResponse<Array<SpeciesDTO>>>) => {
        for (let i = 0; i < http.response.result.length; i++) {
          let speciesDto = http.response.result[i];
          this.speciesList.push({
            value: speciesDto.uri,
            text: speciesDto.label
          });
        }
      })
      .catch(this.$opensilex.errorHandler);
  }

  validateForm() {
    let validatorRef: any = this.$refs.validatorRef;
    return validatorRef.validate();
  }

  onValidateSubmit() {
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
    let validatorRef: any = this.$refs.validatorRef;
    validatorRef.validate().then(isValid => {
      if (isValid) {
        if (this.uriGenerated && !this.editMode) {
          this.form.uri = null;
        }

        this.onValidateSubmit()
          .then(() => {
            this.$nextTick(() => {
              let modalRef: any = this.$refs.modalRef;
              modalRef.hide();
            });
          })
          .catch(error => {
            if (error.status == 409) {
              console.error("experiment already exists", error);
              this.$opensilex.errorHandler(
                error,
                this.$i18n.t(
                  "component.experiment.errors.experiment-already-exists"
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
