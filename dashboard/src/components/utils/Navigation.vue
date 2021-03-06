/*
Copyright 2020 SkillTree

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
<template>
  <div class="container-fluid px-0" data-cy="nav">
    <div class="row skills-nav no-gutters">
      <div class="col-md-auto border rounded pt-3 pr-0 bg-light">
        <div class="mb-3 ml-3 text-secondary">
          <b-row no-gutters>
            <b-col v-if="smallScreenMode || !collapsed">
              <span class="h6 text-uppercase mr-2" v-if="!collapsed || smallScreenMode">Navigation</span>
            </b-col>
            <b-col :class="{ 'text-right' : !collapsed }">
              <div v-if="!smallScreenMode" :class="{ 'pr-2 pl-3' : !collapsed }">
                <b-button v-if="!smallScreenMode" size="sm" variant="outline-secondary" @click="flipCollapsed"
                          class="py-0 text-primary" style="border-color: #d8d8d9;" data-cy="navCollapseOrExpand" v-b-tooltip.hover :title="collapsed ? 'Expand Navigation' : 'Collapse Navigation'">
                  <i v-if="!collapsed" class="fas fa-compress-alt"/><i v-else class="fas fa-expand-alt"/>
                </b-button>
              </div>
              <div v-if="smallScreenMode" class="pr-2">
                <b-button v-b-toggle.menu-collapse-control variant="outline-secondary" size="sm" class="mb-1"
                          data-cy="navSmallScreenExpandMenu">
                  <i class="fas fa-bars"/>
                </b-button>
              </div>
            </b-col>
          </b-row>
        </div>

        <!-- bootstrap didn't handle vertical menus well so rolling out our own-->
        <b-collapse id="menu-collapse-control" :visible="!smallScreenMode">
          <ul class="p-0" style="list-style: none;">
            <router-link :to="{ name: navItem.page }" tag="li" class="mb-1 p-2 text-primary" v-for="(navItem) of navItems" :key="navItem.name"
                :data-cy="`nav-${navItem.name}`"
                @click.native="navigate(`${navItem.name}`)"
                v-b-tooltip="{ title: navItem.name, placement: 'right', variant: 'primary', disabled: !collapsed }"
                :class="{'bg-primary': menuSelections.get(navItem.name), 'text-light': menuSelections.get(navItem.name), 'select-cursor': !menuSelections.get(navItem.name), 'disabled': navItem.isDisabled}">
              <div class="text-truncate ml-3" :class="{'mr-4': !collapsed}">
                  <i :class="navItem.iconClass" class="fas"
                     style="min-width: 1.7rem;"/> <span v-if="!collapsed || smallScreenMode">{{ navItem.name }}</span>
                  <i v-if="navItem.isDisabled" class="fas fa-exclamation-circle text-warning ml-1" style="pointer-events: all;" v-b-tooltip.hover="navItem.msg"/>
              </div>
            </router-link>
          </ul>
        </b-collapse>
      </div>
      <div class="col-md">
        <div v-for="(navItem) of navItems" :key="navItem.name">
          <div v-if="menuSelections.get(navItem.name)">
            <div class="container-fluid pb-4">
              <router-view></router-view>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'Navigation',
    props: ['navItems'],
    data() {
      return {
        collapsed: false,
        menuSelections: [],
        windowWidth: 0,
      };
    },
    created() {
      this.buildNewMenuMapWhenPropsChange(this.navItems);
      window.addEventListener('resize', this.handleResize);
      this.handleResize();
    },
    mounted() {
      this.collapsed = this.getCollapsedFromLocalStorage();
    },
    destroyed() {
      window.removeEventListener('resize', this.handleResize);
    },
    watch: {
      // must watch the input because the user is allowed to modify the menu any time
      navItems(newValue) {
        this.buildNewMenuMapWhenPropsChange(newValue);
      },
      $route: function routeChange() {
        this.buildNewMenuMapWhenPropsChange(this.navItems);
      },
    },
    methods: {
      getCollapsedFromLocalStorage() {
        const storageCollapsed = localStorage.skillsNavCollapsed;
        this.collapsed = storageCollapsed === 'true' ? Boolean(storageCollapsed) : false;
        return this.collapsed;
      },
      flipCollapsed() {
        this.collapsed = !this.collapsed;
        localStorage.skillsNavCollapsed = this.collapsed;
      },
      navigate(selectedKey) {
        if (this.smallScreenMode) {
          this.$root.$emit('bv::toggle::collapse', 'menu-collapse-control');
        }
        const menuSelectionsTemp = this.buildNewMenuMap(selectedKey);
        this.menuSelections = menuSelectionsTemp;
      },
      buildNewMenuMapWhenPropsChange(navigationItems) {
        const routeName = this.$route.name;
        if (navigationItems && navigationItems.length > 0) {
          const navItem = navigationItems.find((item) => item.page === routeName);
          this.menuSelections = this.buildNewMenuMap(navItem ? navItem.name : navigationItems[0].name);
        }
      },
      buildNewMenuMap(selectedKey) {
        const menuSelectionsTemp = new Map();
        this.navItems.forEach((navItem) => {
          menuSelectionsTemp.set(navItem.name, false);
        });
        menuSelectionsTemp.set(selectedKey, true);

        return menuSelectionsTemp;
      },
      handleResize() {
        this.windowWidth = window.innerWidth;
      },
    },
    computed: {
      smallScreenMode() {
        // 768 matches md in bootstrap
        return this.windowWidth < 768;
      },
    },
  };
</script>

<style scoped>
  .select-cursor {
    cursor: pointer;
  }

  @media (min-width: 768px) {
    .skills-nav {
      min-height: calc(100vh - 10rem);
    }
  }

</style>
