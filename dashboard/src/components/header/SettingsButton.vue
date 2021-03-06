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
  <b-dropdown right variant="link" no-caret>
    <template slot="button-content">
      <b-avatar variant="primary" :text="avatarTxt" size="sm"></b-avatar>
      <span class="d-inline-block text-truncate userName ml-1 font-weight-bold">{{ displayName }}</span>
    </template>
    <b-dropdown-item href="#"  @click="gotoSettings">
      <span class="text-gray-700"> <i class="fas fa-cog"/><span class="link-name">Settings</span></span>
    </b-dropdown-item>
    <b-dropdown-divider />
    <b-dropdown-item v-if="isFormAuthenticatedUser" href="#" @click="signOut">
      <span class="text-gray-700"> <i class="fas fa-sign-out-alt"/><span class="link-name">Log Out</span></span>
    </b-dropdown-item>
  </b-dropdown>
</template>

<script>
  export default {
    name: 'SettingsButton',
    data() {
      return {
        userInfoLoaded: false,
      };
    },
    computed: {
      userInfo() {
        return this.$store.getters.userInfo;
      },
      isAuthenticatedUser() {
        return this.$store.getters.isAuthenticated;
      },
      isFormAuthenticatedUser() {
        return this.isAuthenticatedUser && !this.$store.getters.isPkiAuthenticated;
      },
      avatarTxt() {
        const { userInfo } = this.$store.getters;
        return `${userInfo.first[0]}${userInfo.last[0]}`.toUpperCase();
      },
      displayName() {
        const { userInfo } = this.$store.getters;
        let displayName = userInfo.nickname;
        if (!displayName) {
          displayName = `${userInfo.first} ${userInfo.last}`;
        }
        return displayName;
      },
    },
    methods: {
      gotoSettings() {
        this.$router.push({ name: 'GeneralSettings' });
      },
      signOut() {
        this.$store.dispatch('logout');
      },
    },
  };
</script>

<style scoped>
  .userName {
    max-width: 5rem;
    vertical-align: top
  }

  @media (min-width: 576px) {
    .userName {
      max-width: 9rem;
    }
  }

  @media (min-width: 1200px) {
    .userName {
      max-width: 12rem;
    }
  }

  .text-gray-700 > i {
    width: 1.6rem;
  }

</style>
