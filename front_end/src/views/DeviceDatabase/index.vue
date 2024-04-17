<template>
  <div class="iframe-container">
    <iframe src="https://demo.mtruning.club/#/chart/preview/934431161235673088" frameborder="0"></iframe>
  </div>
</template>

<script>
export default {
  name: 'ExternalSite'
  // 你可以在这里添加逻辑
}
</script>

<style>
.iframe-container iframe {
  width: 100%;
  height: 100vh; /* 视窗的100%高度 */
  border: none;
}

/* 确保iframe容器填满父元素 */
.iframe-container {
  width: 100%;
  height: 100%;
}
</style>
