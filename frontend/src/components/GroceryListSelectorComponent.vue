<script setup lang="ts">
import type {GroceryList} from "@/model/GroceryList.ts";

const listId = defineModel<number | null | undefined>()

const props = withDefaults(defineProps<{
  lists?: GroceryList[]
  showAllOption?: boolean
}>(), {
  lists: () => [],
  showAllOption: false
})
</script>

<template>
  <select v-model="listId" class="border border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-gray-100 rounded h-9 px-2">
    <option v-if="showAllOption" :value="undefined" :selected="listId === undefined">Alle Listen</option>
    <option :value="null" :selected="listId === null">Ohne Liste</option>
    <option v-for="list in lists" :key="list.id" :value="list.id" :selected="list.id == listId">{{ list.name }}</option>
  </select>
</template>
