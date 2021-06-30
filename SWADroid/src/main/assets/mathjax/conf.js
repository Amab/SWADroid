window.MathJax = {
  options: {
    enableMenu: false,
    ignoreHtmlClass: 'tex2jax_ignore',
    processHtmlClass: 'tex2jax_process'
  },
  tex: {
    inlineMath: [ ['$','$'], ["\\(","\\)"] ],
    processEscapes: true,
    packages: ['base', 'ams', 'noerrors', 'noundefined']
  },
  loader: {
    load: ['[tex]/noerrors']
  }
};
