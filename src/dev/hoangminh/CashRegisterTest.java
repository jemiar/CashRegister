package dev.hoangminh;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CashRegisterTest {

    @Test
    void sum() {
        CashRegister cashRegister = new CashRegister();
        cashRegister.put(new String[]{"put", "1", "2", "3", "4", "5"});
        //Test if sum is calculated correctly
        assertEquals(68, cashRegister.sum());
        //Test if sum is calculated correctly after an invalid command: negative value
        cashRegister.put(new String[]{"put", "1", "-2", "3", "4", "5"});
        assertEquals(68, cashRegister.sum());
        //Test if sum is calculated correctly after an invalid command: invalid command
        cashRegister.put(new String[]{"put", "1", "2", "3", "5"});
        assertEquals(68, cashRegister.sum());
    }

    @Test
    void put() {
        CashRegister cashRegister = new CashRegister();
        int[] expected = {1, 2, 3, 4, 5};
        cashRegister.put(new String[]{"put", "1", "2", "3", "4", "5"});
        //Test if banknotes are updated correctly after put
        assertArrayEquals(expected, cashRegister.getBanknotes());
        cashRegister.put(new String[]{"put", "1", "-2", "3", "4", "5"});
        //Test if banknotes aren't updated after invalid command
        assertArrayEquals(expected, cashRegister.getBanknotes());
        expected[0] += 1;
        //Test if banknotes are updated correctly after put
        cashRegister.put(new String[]{"put", "1", "0", "0", "0", "0"});
        assertArrayEquals(expected, cashRegister.getBanknotes());
    }

    @Test
    void take() {
        CashRegister cashRegister = new CashRegister();
        int[] expected = {1, 2, 3, 4, 5};
        cashRegister.put(new String[]{"put", "1", "2", "3", "4", "5"});
        cashRegister.take(new String[]{"take", "0", "0", "0", "0", "6"});
        //Test if sum is not changed after an invalid command
        assertEquals(68, cashRegister.sum());
        //Test if banknotes aren't updated after invalid take command
        assertArrayEquals(expected, cashRegister.getBanknotes());
        cashRegister.take(new String[]{"take", "1", "-2", "3", "4", "5"});
        //Test if banknotes aren't updated after invalid take command
        assertArrayEquals(expected, cashRegister.getBanknotes());
        expected[0] -= 1;
        cashRegister.take(new String[]{"take", "1", "0", "0", "0", "0"});
        //Test if banknotes are updated after appropriate take command
        assertArrayEquals(expected, cashRegister.getBanknotes());
        //Test if sum is calculated correctly after appropriate take command
        assertEquals(48, cashRegister.sum());
    }

    @Test
    void change() {
        CashRegister cashRegister = new CashRegister();
        int[] expected = {0, 0, 1, 0, 0};
        cashRegister.put(new String[]{"put", "0", "0", "1", "4", "0"});
        cashRegister.change(new String[]{"change", "8"});
        //Test if sum is updated correctly after a feasible change
        assertEquals(5, cashRegister.sum());
        //Test if banknotes are updated correctly after a feasible change
        assertArrayEquals(expected, cashRegister.getBanknotes());
        cashRegister.put(new String[]{"put", "0", "0", "0", "4", "0"});
        cashRegister.change(new String[]{"change", "7"});
        //Test if sum is updated correctly after a feasible change
        assertEquals(6, cashRegister.sum());
        //Test if banknotes are updated correctly after a feasible change
        assertArrayEquals(new int[]{0, 0, 0, 3, 0}, cashRegister.getBanknotes());
        cashRegister.put(new String[]{"put", "0", "0", "1", "1", "0"});
        cashRegister.change(new String[]{"change", "10"});
        //Test if sum is not updated after an infeasible change
        assertEquals(13, cashRegister.sum());
        //Test if banknotes are not updated after an infeasible change
        assertArrayEquals(new int[]{0, 0, 1, 4, 0}, cashRegister.getBanknotes());
    }
}