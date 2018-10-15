console.log('package-tracker.js loaded');

$( document ).ready(function() {
  document.body.style.overflow = "auto";
  document.body.style.backgroundColor = "#2f3534";
  console.log("Ready! Ready! Ready! Ready! Ready! Ready!");
});

function newSlice() {
  console.log('a new slice was added.');

  $(".v-scrollable").animate({scrollLeft: $('.slice').length * 400}, 800);
}
