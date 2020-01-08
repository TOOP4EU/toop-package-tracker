/*
 * Copyright (C) 2018-2020 toop.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
