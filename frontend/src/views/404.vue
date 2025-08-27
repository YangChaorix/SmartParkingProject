<template>
  <div class="error-page">
    <div class="background-container">
      <img class="background-image" src="@/assets/imgs/404.jpg" alt="404 Error">
      <div class="overlay"></div>
    </div>
    <div class="content">
      <router-link class="back-button" to="/">返回首页</router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: "404",
  data() {
    return {}
  },
  created() {
  },
  methods: {}
}
</script>

<style scoped>
.error-page {
  position: relative;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  background: #1a1a1a;
}

.background-container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.background-image {
  min-width: 100%;
  min-height: 100%;
  width: auto;
  height: auto;
  object-fit: contain;
  max-width: none;
  position: relative;
  z-index: 1;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, transparent 70%, rgba(0, 0, 0, 0.7));
  z-index: 2;
}

.content {
  position: absolute;
  bottom: 5%;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  z-index: 3;
  width: 100%;
  padding: 0 20px;
}

.back-button {
  display: inline-block;
  padding: 1rem 3rem;
  background: linear-gradient(135deg, #ff6b6b, #4ecdc4);
  color: white;
  text-decoration: none;
  border-radius: 30px;
  font-size: 1.2rem;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.back-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.4);
  background: linear-gradient(135deg, #ff8585, #5ce7cd);
}

@media (max-width: 768px) {
  .content {
    bottom: 8%;
  }

  .back-button {
    padding: 0.8rem 2.5rem;
    font-size: 1.1rem;
  }
}
</style>