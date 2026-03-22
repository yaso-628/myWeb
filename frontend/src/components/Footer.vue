<template>
  <footer class="footer">
    <p>© {{ year }} MyWeb by 茉莉白</p>
  </footer>
</template>

<script setup>
import { computed } from 'vue'

const year = computed(() => new Date().getFullYear())
</script>

<style scoped>
.footer {
  padding: 1.5rem 2rem;
  text-align: center;
  color: var(--color-text-muted);
  font-size: 0.875rem;
  border-top: 1px solid var(--color-border);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
}
.dark .footer {
  background: rgba(31, 41, 55, 0.9);
}
</style>
