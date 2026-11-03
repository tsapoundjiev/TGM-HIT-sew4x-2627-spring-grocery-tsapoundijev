import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig(({mode}) => {
  const frontendPort = 5173;
  const backendURL = 'http://localhost:8080';

  return {
    plugins: [
      vue(),
      // The devtools floating panel can overlap page content and intercept
      // clicks, which breaks Cypress's actionability checks — skip it in test mode.
      ...(mode === 'test' ? [] : [vueDevTools()]),
      tailwindcss(),
    ],
    server: {
      port: frontendPort,
      proxy: {
        '/api': {target: backendURL, changeOrigin: true}
      }
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      },
    }
  };
})
