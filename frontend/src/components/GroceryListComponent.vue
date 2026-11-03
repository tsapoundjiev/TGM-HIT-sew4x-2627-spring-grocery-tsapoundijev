<script setup lang="ts">
import type {GroceryItem} from "@/model/GroceryItem.ts"
import type {GroceryList} from "@/model/GroceryList.ts";
import GroceryItemComponent from "@/components/GroceryItemComponent.vue";
import GroceryListSelectorComponent from "@/components/GroceryListSelectorComponent.vue";
import {ExclamationCircleIcon, PlusIcon, XMarkIcon} from "@heroicons/vue/24/outline";

const dummyItem = <GroceryItem>{id: 1, name: "Apfel", amount: 1, collected: false, listId: null};
const dummyList = [<GroceryList>{id: 1, name: "List"}];
</script>
<template>
  <nav class="sticky top-0 z-10 bg-white dark:bg-gray-900 flex flex-wrap items-center justify-between gap-3 p-3">
    <div class="flex flex-wrap items-center gap-3">
      <h1 class="text-xl font-semibold flex items-center gap-2"><img src="/icon-tgm.svg" style="height: 1em;" alt="logo"> Einkaufsliste</h1>
      <GroceryListSelectorComponent :showAllOption="true"/>
    </div>
    <p class="text-sm flex-auto text-right">TODO von TODO Artikel eingesammelt</p>
    <div class="absolute bottom-0 inset-x-0 h-1 bg-gray-200 dark:bg-gray-700">
      <div class="h-full bg-blue-600 dark:bg-blue-500" :style="{ width: '50%' /* Prozent collected */ }"></div>
    </div>
  </nav>

  <GroceryItemComponent :item="dummyItem" :lists="dummyList" />

  <div class="fixed bottom-0 inset-x-0 bg-white dark:bg-gray-900 flex gap-2 justify-between items-center border-t border-gray-200 dark:border-gray-700 p-2">
    <div class="w-4 h-4 m-2.5"></div>

    <div class="flex flex-1 flex-wrap gap-2">
      <div class="w-full sm:w-12">
        <input type="number" placeholder="Anzahl" v-on:keyup.enter="console.error('createItem not implemented')" class="border border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-gray-100 rounded px-2 h-9 w-full">
      </div>

      <div class="w-full sm:flex-1 relative">
        <input type="text" placeholder="Name" v-on:keyup.enter="console.error('createItem not implemented')" class="border rounded px-2 h-9 w-full dark:bg-gray-800 dark:text-gray-100" :class="false /* EK Validation: Sollte true sein, wenn ein Validation Fehler auftritt */ ? 'border-red-600 dark:border-red-500 pr-8' : 'border-gray-300 dark:border-gray-600'">
        <span v-if="false /* EK Validation: Sollte true sein, wenn ein Validation Fehler auftritt */" class="absolute right-2 top-1/2 -translate-y-1/2 text-red-600 dark:text-red-400" :title="'' /* EK Validation: Fehlermeldungen hier zusammenfügen */">
          <ExclamationCircleIcon class="w-5 h-5"/>
        </span>
      </div>

      <div class="w-full sm:w-auto">
        <GroceryListSelectorComponent/>
      </div>
    </div>

    <div class="flex flex-col sm:flex-row items-center justify-around sm:justify-center gap-2">
      <button type="button" aria-label="Erstellen" class="border border-blue-600 dark:border-blue-400 text-blue-600 dark:text-blue-400 rounded h-9 w-9 flex items-center justify-center hover:bg-blue-50 dark:hover:bg-blue-950 disabled:opacity-50 disabled:hover:bg-transparent" @click="console.error('createNewItem not implemented')" :disabled="false /* EK Validation: Sollte true sein, wenn ein Validation Fehler auftritt */"><PlusIcon class="w-5 h-5"/></button>
      <button type="button" aria-label="Abbrechen" class="border border-gray-400 dark:border-gray-500 text-gray-600 dark:text-gray-400 rounded h-9 w-9 flex items-center justify-center hover:bg-gray-100 dark:hover:bg-gray-800" @click="console.error('clearNewItem not implemented')"><XMarkIcon class="w-5 h-5"/></button>
      <div class="w-9 h-9"></div>
    </div>
  </div>
</template>
