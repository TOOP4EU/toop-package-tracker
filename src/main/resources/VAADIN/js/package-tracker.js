console.log('package-tracker.js loaded');

function newSlice() {
    console.log('a new slice was added.');

    $(".v-scrollable").animate({scrollLeft: $('.slice').length * 400}, 800);
}
