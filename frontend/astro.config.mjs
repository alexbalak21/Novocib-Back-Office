// @ts-check
import { defineConfig } from 'astro/config';
const endpoint = import.meta.env.PUBLIC_API_URL;


export default defineConfig({
  vite: {
    server: {
      proxy: {
        '/graphql': {
          target: endpoint,
          changeOrigin: true,
        },
      },
    },
  },
});
