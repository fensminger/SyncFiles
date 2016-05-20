"use strict";
var testing_1 = require('@angular/core/testing');
var testing_2 = require('@angular/compiler/testing');
var testing_3 = require('@angular/platform-browser/testing');
var by_1 = require('@angular/platform-browser/src/dom/debug/by');
var common_1 = require('@angular/common');
var demo_form_with_custom_validations_1 = require('../../app/ts/forms/demo_form_with_custom_validations');
testing_1.describe('DemoFormWithCustomValidations', function () {
    var el, input, form;
    testing_1.beforeEachProviders(function () { return [common_1.FormBuilder]; });
    function createComponent(tcb) {
        return tcb.createAsync(demo_form_with_custom_validations_1.DemoFormWithCustomValidations).then(function (fixture) {
            el = fixture.debugElement.nativeElement;
            input = fixture.debugElement.query(by_1.By.css("input")).nativeElement;
            form = fixture.debugElement.query(by_1.By.css("form")).nativeElement;
            fixture.detectChanges();
            return fixture;
        });
    }
    testing_1.it('displays errors with no sku', testing_1.async(testing_1.inject([testing_2.TestComponentBuilder], function (tcb) {
        return createComponent(tcb).then(function (fixture) {
            input.value = '';
            testing_3.dispatchEvent(input, 'input');
            fixture.detectChanges();
            var msgs = el.querySelectorAll('.ui.error.message');
            testing_1.expect(msgs[0]).toHaveText('SKU is invalid');
            testing_1.expect(msgs[1]).toHaveText('SKU is required');
            testing_1.expect(msgs[2]).toHaveText('SKU must begin with 123');
            testing_1.expect(msgs[3]).toHaveText('Form is invalid');
        });
    })));
    testing_1.it('removes the required error when sku has a value', testing_1.async(testing_1.inject([testing_2.TestComponentBuilder], function (tcb) {
        return createComponent(tcb).then(function (fixture) {
            input.value = 'ABC';
            testing_3.dispatchEvent(input, 'input');
            fixture.detectChanges();
            var msgs = el.querySelectorAll('.ui.error.message');
            testing_1.expect(msgs[0]).toHaveText('SKU is invalid');
            testing_1.expect(msgs[1]).toHaveText('SKU must begin with 123');
            testing_1.expect(msgs[2]).toHaveText('Form is invalid');
        });
    })));
    testing_1.it('removes all errors when sku starts with 123', testing_1.async(testing_1.inject([testing_2.TestComponentBuilder], function (tcb) {
        return createComponent(tcb).then(function (fixture) {
            input.value = '123ABC';
            testing_3.dispatchEvent(input, 'input');
            fixture.detectChanges();
            var msgs = el.querySelectorAll('.ui.error.message');
            testing_1.expect(msgs.length).toEqual(0);
        });
    })));
});
