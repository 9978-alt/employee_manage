<script setup>
import Header from "./components/Header.vue"
</script>

<template>
  <div>
    <Header></Header>
    <hr>
    <!-- <router-link to="employee">employee</router-link><br>
    <router-link to="department">department</router-link><br>
    <router-link to="manage">manage</router-link>
    <hr> -->
    <router-view></router-view>
  </div>
</template>

<style scoped>


</style>
