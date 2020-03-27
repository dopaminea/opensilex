<template>
  <div>
    <b-input-group class="mt-3 mb-3" size="sm">
      <b-input-group>
        <b-form-input
          v-model="filterPattern"
          debounce="300"
          :placeholder="$t('component.infrastructure.filter-placeholder')"
        ></b-form-input>
        <template v-slot:append>
          <b-btn :disabled="!filterPattern" variant="primary" @click="filterPattern = ''">
            <font-awesome-icon icon="times" size="sm" />
          </b-btn>
        </template>
      </b-input-group>
    </b-input-group>
    <sl-vue-tree v-model="nodes" @select="displayNodesDetail">
      <template slot="title" slot-scope="{ node }">
        <span class="item-icon">
          <i class="fa fa-file" v-if="node.isLeaf"></i>
          <i class="fa fa-folder" v-if="!node.isLeaf"></i>
        </span>
        <strong v-if="node.data.selected">{{ node.title }}</strong>
        <span v-if="!node.data.selected">{{ node.title }}</span>
        <b-button-group class="tree-button-group" size="sm">
          <b-button
            size="sm"
            v-if="user.hasCredential(credentials.CREDENTIAL_INFRASTRUCTURE_MODIFICATION_ID)"
            @click.prevent="$emit('onEdit', node.data)"
            variant="outline-primary"
          >
            <font-awesome-icon icon="edit" size="sm" />
          </b-button>
          <b-button
            size="sm"
            v-if="user.hasCredential(credentials.CREDENTIAL_INFRASTRUCTURE_MODIFICATION_ID)"
            @click.prevent="$emit('onAddChild', node.data.uri)"
            variant="outline-success"
          >
            <font-awesome-icon icon="plus" size="sm" />
          </b-button>
          <b-button
            size="sm"
            v-if="user.hasCredential(credentials.CREDENTIAL_INFRASTRUCTURE_DELETE_ID)"
            @click.prevent="$emit('onDelete', node.data.uri)"
            variant="danger"
          >
            <font-awesome-icon icon="trash-alt" size="sm" />
          </b-button>
        </b-button-group>
      </template>
    </sl-vue-tree>
    <div class="container" v-if="selected != null">
      <div class="row">
        <div class="col-sm">URI</div>
        <div class="col-sm">{{selected.uri}}</div>
      </div>
      <div class="row">
        <div class="col-sm">Name</div>
        <div class="col-sm">{{selected.name}}</div>
      </div>
      <div class="row">
        <div class="col-sm">Type</div>
        <div class="col-sm">{{selected.type}}</div>
      </div>
      <div class="row">
        <div class="col-sm">Users</div>
        <div class="col-sm">
          <div v-for="user in selected.users" v-bind:key="user.uri">{{user.email}}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop } from "vue-property-decorator";
import Vue from "vue";
import HttpResponse, { OpenSilexResponse } from "../../lib/HttpResponse";
import {
  InfrastructuresService,
  ResourceTreeDTO,
  InfrastructureGetDTO
} from "opensilex-core/index";

@Component
export default class InfrastructureTree extends Vue {
  $opensilex: any;
  $store: any;
  service: InfrastructuresService;

  get user() {
    return this.$store.state.user;
  }

  get credentials() {
    return this.$store.state.credentials;
  }

  private filterPatternValue: any = "";
  set filterPattern(value: string) {
    this.filterPatternValue = value;
    this.refresh();
  }

  get filterPattern() {
    return this.filterPatternValue;
  }

  created() {
    this.service = this.$opensilex.getService(
      "opensilex-core.InfrastructuresService"
    );

    this.refresh();
  }

  refresh() {
    this.service
      .searchInfrastructuresTree(
        this.user.getAuthorizationHeader(),
        this.filterPattern
      )
      .then((http: HttpResponse<OpenSilexResponse<Array<ResourceTreeDTO>>>) => {
        let treeNode = [];

        let first = true;
        for (let i in http.response.result) {
          let resourceTree: ResourceTreeDTO = http.response.result[i];
          let node = this.dtoToNode(resourceTree, first);
          treeNode.push(node);
          if (first) {
            this.displayNodeDetail(node);
            first = false;
          }
        }

        this.nodes = treeNode;

        this.$router
          .push({
            path: this.$route.fullPath,
            query: {
              filterPattern: encodeURI(this.filterPattern)
            }
          })
          .catch(function() {});
      })
      .catch(this.$opensilex.errorHandler);
  }

  private dtoToNode(dto: ResourceTreeDTO, selected: boolean) {
    let isLeaf = dto.children.length == 0;

    let childrenDTOs = [];
    if (!isLeaf) {
      for (let i in dto.children) {
        childrenDTOs.push(this.dtoToNode(dto.children[i], false));
      }
    }
    return {
      title: dto.name,
      data: dto,
      isLeaf: isLeaf,
      children: childrenDTOs,
      isExpanded: true,
      isSelected: selected,
      isDraggable: false,
      isSelectable: true
    };
  }

  public nodes = [];

  private selected: InfrastructureGetDTO = null;

  public displayNodeDetail(node: any) {
    this.displayNodesDetail([node]);
  }

  public displayNodesDetail(nodes: any[]) {
    if (nodes.length > 0) {
      let node = nodes[nodes.length - 1];
      this.service
        .getInfrastructure(this.user.getAuthorizationHeader(), node.data.uri)
        .then((http: HttpResponse<OpenSilexResponse<InfrastructureGetDTO>>) => {
          let detailDTO: InfrastructureGetDTO = http.response.result;
          this.selected = detailDTO;
          this.$emit("onSelect", detailDTO);
        });
    }
  }
}
</script>

<style scoped lang="scss">
.badge {
  float: right;
}
</style>
