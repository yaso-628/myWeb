export default {
  mounted(el) {
    const pos = getComputedStyle(el).position
    if (pos === 'static') el.style.position = 'relative'
    el.style.overflow = 'hidden'
    el.addEventListener('click', (e) => {
      const rect = el.getBoundingClientRect()
      const x = e.clientX - rect.left
      const y = e.clientY - rect.top
      const ripple = document.createElement('span')
      ripple.className = 'ripple-effect'
      ripple.style.cssText = `left:${x}px;top:${y}px`
      el.appendChild(ripple)
      ripple.offsetWidth
      ripple.classList.add('active')
      setTimeout(() => ripple.remove(), 600)
    })
  }
}
